package com.example.onlinejudge.service;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.models.Difficulty;
import com.example.onlinejudge.models.Status;
import com.example.onlinejudge.models.TestCase;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public interface SubmissionService {

    List<TestResultDto> compileAndRun(String userId, String questionId, SubmissionRequestDto submissionRequestDto) throws Exception;
    SubmissionResultDto submitCode(String userId, String questionId, SubmissionRequestDto submissionRequestDto) throws Exception;
    SubmissionDto getSubmissionDetail(String submissionId);
    PagedSubmissionResponse getSubmissionsByFilter(String userId, String questionId,
              String status, List<String> languages, Integer pageNo, Integer pageSize);
}
