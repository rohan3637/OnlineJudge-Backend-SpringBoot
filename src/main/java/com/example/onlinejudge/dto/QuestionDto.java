package com.example.onlinejudge.dto;

import com.example.onlinejudge.models.Difficulty;
import com.example.onlinejudge.models.Submission;
import com.example.onlinejudge.models.Topic;
import com.example.onlinejudge.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {

    private String questionId;
    private String title;
    private List<Topic> topics;
    private String description;
    private Difficulty difficulty;
    private Integer totalSubmission;
    private Integer correctSubmission;
    private List<TestCaseDto> testCaseDtos;
    private String authorSolution;
    private String hints;
}
