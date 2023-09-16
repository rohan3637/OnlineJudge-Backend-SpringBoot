package com.example.onlinejudge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResultDto {

    private String input;
    private String actualOutput;
    private String expectedOutput;
    private boolean passed;
}
