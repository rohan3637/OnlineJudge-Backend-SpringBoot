package com.example.onlinejudge.service;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.models.Discussion;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuestionService {

    public void createQuestion(String userId, CreateQuestionDto createQuestionDto);

    public PagedQuestionResponse getAllQuestionsByFilters(String userId, List<String> topics,
                                                          List<String> difficulties, String searchQuery, Integer pageNo, Integer pageSize);

    public QuestionDto getQuestionDetails(String questionId);

    public QuestionDto updateQuestion(String userId, String questionId, CreateQuestionDto createQuestionDto);

    public void deleteQuestion(String questionId, String userId);

}
