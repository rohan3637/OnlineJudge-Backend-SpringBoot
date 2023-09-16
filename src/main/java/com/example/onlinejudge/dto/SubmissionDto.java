package com.example.onlinejudge.dto;

import com.example.onlinejudge.models.Langauge;
import com.example.onlinejudge.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDto {

    private String submissionId;
    private UserResponseDto userResponseDto;
    private QuestionResponseDto questionDto;
    private Langauge langauge;
    private Status status;
    private String codeContent;
    private LocalDateTime submissionTime;
}
