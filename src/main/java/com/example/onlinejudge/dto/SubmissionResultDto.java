package com.example.onlinejudge.dto;

import com.example.onlinejudge.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubmissionResultDto {

    private Status status;
    private Integer testCasesPassed;
    private Integer totalTestCases;
    private TestResultDto failedTestCase;
}
