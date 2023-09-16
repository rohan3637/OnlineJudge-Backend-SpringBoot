package com.example.onlinejudge.controller;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/compile")
    public ResponseEntity<List<TestResultDto>> compileAndRun(
            @RequestParam String userId,
            @RequestParam String questionId,
            @RequestBody SubmissionRequestDto submissionRequestDto
    ) throws Exception {
        List<TestResultDto> resultDtos = submissionService.compileAndRun(userId, questionId, submissionRequestDto);
        return ResponseEntity.ok(resultDtos);
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmissionResultDto> submitCode(
            @RequestParam String userId,
            @RequestParam String questionId,
            @RequestBody SubmissionRequestDto submissionRequestDto
    ) throws Exception {
        SubmissionResultDto submissionResultDto = submissionService.submitCode(userId, questionId, submissionRequestDto);
        return ResponseEntity.ok(submissionResultDto);
    }

    @GetMapping("/get_submission")
    public ResponseEntity<SubmissionDto> getSubmission(@RequestParam String submissionId) {
        SubmissionDto submissionDto = submissionService.getSubmissionDetail(submissionId);
        return ResponseEntity.ok(submissionDto);
    }

    @GetMapping("/get_submissions")
    public ResponseEntity<PagedSubmissionResponse> getDiscussions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String questionId,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) String status
    ) {
        PagedSubmissionResponse pagedSubmissionResponse = submissionService.getSubmissionsByFilter(userId,
                questionId, status, languages, pageNo, pageSize);
        return ResponseEntity.ok(pagedSubmissionResponse);
    }
}
