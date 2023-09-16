package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.exception.BadRequestException;
import com.example.onlinejudge.exception.ResourceNotFoundException;
import com.example.onlinejudge.models.*;
import com.example.onlinejudge.repository.*;
import com.example.onlinejudge.service.QuestionService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private QuestionCustomRepository questionCustomRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DiscussionCustomRepository discussionCustomRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createQuestion(String userId, CreateQuestionDto createQuestionDto) {
        Question question = modelMapper.map(createQuestionDto, Question.class);
        List<TestCase> testCases = new ArrayList<>();
        createQuestionDto.getTestCaseDtos().forEach(testCaseDto -> {
            TestCase testCase = new TestCase();
            testCase.setInput(testCaseDto.getInput());
            testCase.setExpectedOutput(testCaseDto.getExpectedOutput());
            testCase.setQuestion(question);
            testCases.add(testCase);
        });
        question.setTestCases(testCases);
        List<Topic> topics = new ArrayList<>();
        createQuestionDto.getTopics().forEach(topicId -> {
            Topic topic = topicRepository.findById(topicId).get();
            topics.add(topic);
        });
        question.setTopics(topics);
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        question.setCorrectSubmission(0);
        question.setTotalSubmission(0);
        question.setAuthor(userOptional.get());
        questionRepository.save(question);
    }

    @Override
    public PagedQuestionResponse getAllQuestionsByFilters(String userId, List<String> topics, List<String> difficulties,
                    String searchQuery, Integer pageNo, Integer pageSize) {
        List<Question> questionList = questionCustomRepository.getQuestionByFilters(userId, topics, difficulties, searchQuery, pageNo, pageSize);
        List<QuestionResponseDto> questionDtos = questionList.parallelStream()
                .map(question -> modelMapper.map(question, QuestionResponseDto.class))
                .toList();
        Integer count = questionCustomRepository.getCountByFilters(userId, topics, difficulties, searchQuery);
        PageInfo pageInfo = new PageInfo(pageNo, pageSize, count);
        return new PagedQuestionResponse(pageInfo, questionDtos);
    }

    @Override
    public QuestionDto getQuestionDetails(String questionId) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        List<TestCaseDto> testCaseDtos = questionOptional.get().getTestCases().stream()
                .map(testCase -> modelMapper.map(testCase, TestCaseDto.class))
                .toList();
        QuestionDto questionDto = modelMapper.map(questionOptional.get(), QuestionDto.class);
        questionDto.setTestCaseDtos(testCaseDtos);
        return questionDto;
    }

    @Override
    public QuestionDto updateQuestion(String userId, String questionId, CreateQuestionDto createQuestionDto) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        Question question = questionOptional.get();
        if(!question.getAuthor().getUserId().equals(userId)) {
            throw new BadRequestException("Only author can edit their question !!");
        }
        List<TestCase> testCases = new ArrayList<>();
        createQuestionDto.getTestCaseDtos().forEach(testCaseDto -> {
            if(testCaseDto.getId() != null) {
                testCaseRepository.deleteById(testCaseDto.getId());
            }
            TestCase testCase = new TestCase();
            testCase.setInput(testCaseDto.getInput());
            testCase.setExpectedOutput(testCaseDto.getExpectedOutput());
            testCase.setQuestion(question);
            testCases.add(testCase);
        });
        question.setTestCases(testCases);
        question.setTitle(createQuestionDto.getTitle());
        question.setDescription(createQuestionDto.getDescription());
        question.setHints(createQuestionDto.getHints());
        question.setDifficulty(createQuestionDto.getDifficulty());
        List<Topic> topics = new ArrayList<>();
        createQuestionDto.getTopics().forEach(topicId -> {
            Topic topic = topicRepository.findById(topicId).get();
            topics.add(topic);
        });
        question.setTopics(topics);
        Question savedQuestion = questionRepository.save(question);
        return modelMapper.map(savedQuestion, QuestionDto.class);
    }

    @Override
    public void deleteQuestion(String userId, String questionId) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        Question question = questionOptional.get();
        if(!question.getAuthor().getUserId().equals(userId)) {
            throw new BadRequestException("Only author can delete their question !!");
        }
        deleteTopicMappingsForQuestion(questionId);
        questionRepository.delete(question);
    }

    private void deleteTopicMappingsForQuestion(String questionId) {
        // Perform a native SQL delete query to remove topic_mapping records for the given questionId
        String sql = "DELETE FROM onlinejudge.topic_mapping WHERE question = :questionId";
        entityManager.createNativeQuery(sql)
                .setParameter("questionId", questionId)
                .executeUpdate();
    }
}
