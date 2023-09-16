package com.example.onlinejudge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedQuestionResponse {

    private PageInfo pageInfo;
    private List<QuestionResponseDto> questionResponseDtos;
}
