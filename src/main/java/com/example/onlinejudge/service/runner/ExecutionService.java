package com.example.onlinejudge.service.runner;

import com.example.onlinejudge.dto.TestResultDto;

import java.io.IOException;

public interface ExecutionService {

    void compile(String codeContent, String fileName) throws Exception;
    String execute(String codeContent, String input, String fileName) throws Exception;
}
