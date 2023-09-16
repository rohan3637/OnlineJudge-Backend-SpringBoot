package com.example.onlinejudge.dto;

import com.example.onlinejudge.models.Difficulty;
import com.example.onlinejudge.models.TestCase;
import com.example.onlinejudge.models.Topic;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionDto {

    private String title;
    private String description;
    private String hints;
    private Difficulty difficulty;
    private List<Integer> topics;
    private List<TestCaseDto> testCaseDtos;
}
