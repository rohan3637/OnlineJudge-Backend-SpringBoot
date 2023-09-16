package com.example.onlinejudge.dto;

import com.example.onlinejudge.models.Langauge;
import lombok.Data;

@Data
public class SubmissionRequestDto {

    private Langauge language;
    private String codeContent;
}
