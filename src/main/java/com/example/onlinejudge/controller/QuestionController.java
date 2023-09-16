package com.example.onlinejudge.controller;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/add_question")
    public ResponseEntity<ApiResponse> addQuestion(@RequestParam String userId, @RequestBody CreateQuestionDto createQuestionDto) {
        questionService.createQuestion(userId, createQuestionDto);
        ApiResponse apiResponse = new ApiResponse("Question added successfully !!", true);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update_question")
    public ResponseEntity<QuestionDto> updateQuestion(
            @RequestParam String userId,
            @RequestParam String questionId,
            @RequestBody CreateQuestionDto createQuestionDto
    ) {
        QuestionDto questionDto = questionService.updateQuestion(userId, questionId, createQuestionDto);
        return new ResponseEntity<>(questionDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete_question")
    public ResponseEntity<ApiResponse> deleteQuestion(@RequestParam String userId, @RequestParam String questionId) {
        questionService.deleteQuestion(userId, questionId);
        ApiResponse apiResponse = new ApiResponse("Question deleted successfully !!", true);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/get_questions")
    public ResponseEntity<PagedQuestionResponse> getAllQuestions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) List<String> topics,
            @RequestParam(required = false) List<String> difficulties,
            @RequestParam(defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) String searchQuery
    ) {
        PagedQuestionResponse questionResponseDtos = questionService.getAllQuestionsByFilters(
                userId, topics, difficulties, searchQuery, pageNo, pageSize);
        return new ResponseEntity<>(questionResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/get_question_details")
    public ResponseEntity<QuestionDto> getQuestionDetails(@RequestParam String questionId) {
        QuestionDto questionDto = questionService.getQuestionDetails(questionId);
        return new ResponseEntity<>(questionDto, HttpStatus.OK);
    }

}
