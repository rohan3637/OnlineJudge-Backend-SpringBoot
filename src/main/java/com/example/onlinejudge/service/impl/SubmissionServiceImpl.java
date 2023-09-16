package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.exception.BadRequestException;
import com.example.onlinejudge.exception.ResourceNotFoundException;
import com.example.onlinejudge.models.*;
import com.example.onlinejudge.repository.QuestionRepository;
import com.example.onlinejudge.repository.SubmissionCustomRepository;
import com.example.onlinejudge.repository.SubmissionRepository;
import com.example.onlinejudge.repository.UserRepository;
import com.example.onlinejudge.service.runner.*;
import com.example.onlinejudge.service.SubmissionService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SubmissionCustomRepository submissionCustomRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private CPPExecutionService cppExecutionService;

    @Override
    public List<TestResultDto> compileAndRun(String userId, String questionId, SubmissionRequestDto submissionRequestDto) throws Exception {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        List<TestCase> testCases = questionOptional.get().getTestCases().subList(0, 2);
        return runCodeOnTestCases(submissionRequestDto.getLanguage().name(), submissionRequestDto.getCodeContent(), testCases);
    }

    @Override
    public SubmissionResultDto submitCode(String userId, String questionId, SubmissionRequestDto submissionRequestDto) throws Exception {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        List<TestCase> testCases = questionOptional.get().getTestCases();
        List<TestResultDto> testResultDtos = runCodeOnTestCases(submissionRequestDto.getLanguage().name(), submissionRequestDto.getCodeContent(), testCases);
        Integer testCasesPassed = 0;
        TestResultDto failedTestCase = null;
        for(TestResultDto testResultDto : testResultDtos) {
            if(testResultDto.isPassed()) testCasesPassed++;
            else if(failedTestCase == null) failedTestCase = testResultDto;
        }
        Status status = (testCasesPassed == testCases.size() ? Status.ACCEPTED : Status.REJECTED);
        Submission submission = modelMapper.map(submissionRequestDto, Submission.class);
        submission.setQuestion(questionOptional.get());
        submission.setLangauge(submissionRequestDto.getLanguage());
        submission.setUser(userOptional.get());
        submission.setSubmissionTime(LocalDateTime.now());
        submission.setTestCasePassed(testCasesPassed);
        submission.setStatus(status);

        Question question = questionOptional.get();
        question.setTotalSubmission(question.getTotalSubmission() + 1);
        if(status == Status.ACCEPTED) {
            question.setCorrectSubmission(question.getCorrectSubmission() + 1);
            updateUserScore(userOptional.get(), question);
        }
        questionRepository.save(question);
        submissionRepository.save(submission);

        return new SubmissionResultDto(status, testCasesPassed, testCases.size(), failedTestCase);
    }

    private void updateUserScore(User user, Question question) {
        List<Submission> submissions = submissionRepository.findByUserAndQuestion(user, question);
        for(Submission submission : submissions) {
            if (submission.getStatus() == Status.ACCEPTED) {
                Integer score = 0;
                if (question.getDifficulty() == Difficulty.EASY) score = 10;
                else if (question.getDifficulty() == Difficulty.MEDIUM) score = 20;
                else score = 20;
                user.setScore(user.getScore() + score);
                break;
            }
        }
    }

    @Override
    public SubmissionDto getSubmissionDetail(String submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if(submissionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Submission", "id", submissionId);
        }
        Submission submission = submissionOptional.get();
        QuestionResponseDto questionDto = modelMapper.map(submission.getQuestion(), QuestionResponseDto.class);
        UserResponseDto userResponseDto = modelMapper.map(submission.getUser(), UserResponseDto.class);
        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setSubmissionId(submissionId);
        submissionDto.setStatus(submission.getStatus());
        submissionDto.setSubmissionTime(submission.getSubmissionTime());
        submissionDto.setLangauge(submission.getLangauge());
        submissionDto.setCodeContent(submission.getCodeContent());
        submissionDto.setQuestionDto(questionDto);
        submissionDto.setUserResponseDto(userResponseDto);
        return submissionDto;
    }

    @Override
    public PagedSubmissionResponse getSubmissionsByFilter(String userId, String questionId, String status,
                     List<String> languages, Integer pageNo, Integer pageSize) {
        List<Submission> submissions = submissionCustomRepository.getSubmissionByFilter(
                userId, questionId, status, languages, pageNo, pageSize);
        Integer totalCount = submissionCustomRepository.getSubmissionCount(userId, questionId, status, languages);
        List<SubmissionDto> submissionDtos = new ArrayList<>();
        submissions.forEach(submission -> {
            SubmissionDto submissionDto = modelMapper.map(submission, SubmissionDto.class);
            if(questionId == null) submissionDto.setQuestionDto(modelMapper.map(submission.getQuestion(), QuestionResponseDto.class));
            if(userId == null) submissionDto.setUserResponseDto(modelMapper.map(submission.getUser(), UserResponseDto.class));
            submissionDtos.add(submissionDto);
        });
        PageInfo pageInfo = new PageInfo(pageNo, pageSize, totalCount);
        return new PagedSubmissionResponse(pageInfo, submissionDtos);
    }

    private List<TestResultDto> runCodeOnTestCases(String language, String code, List<TestCase> testCases) throws Exception {
        try {
            File tempDir = new File("temp");
            tempDir.mkdirs();
            File codeFile = new File(tempDir, "submission." + getExtensionForLanguage(language));
            // Write code to the generated file
            try (FileWriter fileWriter = new FileWriter(codeFile)) {
                fileWriter.write(code);
            }
            String filePath = codeFile.getAbsolutePath();

            ExecutionService executionService = null;
            if (language == Langauge.JAVA.name()) executionService = new JavaExecutionService();
            else if (language == Langauge.JAVASCRIPT.name()) executionService = new JSExecutionService();
            else if (language == Langauge.CPP.name()) executionService = cppExecutionService;
            else if (language == Langauge.PYTHON.name()) executionService = new PythonExecutionService();
            else throw new BadRequestException("Unsupported language !!");

            executionService.compile(code, filePath);
            List<TestResultDto> results = new ArrayList<>();
            for (TestCase testCase : testCases) {
                String expectedOutput = testCase.getExpectedOutput();
                String standardOutput = executionService.execute(code, testCase.getInput(), filePath);
                boolean isTestCasePassed = standardOutput.trim().equals(expectedOutput.trim());
                results.add(new TestResultDto(testCase.getInput(), standardOutput.trim(), expectedOutput.trim(), isTestCasePassed));
            }
            //.delete();
            return results;
        } catch (Exception ex) {
            throw new RuntimeException("Something went wrong: " + ex.getMessage());
        }
    }

    private String getExtensionForLanguage(String language) {
        language = language.toLowerCase();
        switch (language) {
            case "java": return "java";
            case "cpp": return "cpp";
            case "javascript": return "js";
            case "python": return "py";
            case "c": return "c";
            default:
                throw new BadRequestException("Unsupported language " + language);
        }
    }

}
