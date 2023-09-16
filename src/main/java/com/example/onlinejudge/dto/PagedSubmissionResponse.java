package com.example.onlinejudge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedSubmissionResponse {

    private PageInfo pageInfo;
    private List<SubmissionDto> submissionDtos;
}
