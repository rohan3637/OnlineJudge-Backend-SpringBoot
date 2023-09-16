package com.example.onlinejudge.dto;

import com.example.onlinejudge.models.Difficulty;
import com.example.onlinejudge.models.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {

    private String questionId;
    private String title;
    private List<Topic> topics;
    private Difficulty difficulty;
    private Integer totalSubmission;
    private Integer correctSubmission;
}
