package com.example.onlinejudge.controller;

import com.example.onlinejudge.dto.ApiResponse;
import com.example.onlinejudge.dto.DiscussionDto;
import com.example.onlinejudge.dto.PagedDiscussionResponse;
import com.example.onlinejudge.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping("/post_discussion")
    public ResponseEntity<DiscussionDto> createDiscussion(
            @RequestParam String userId,
            @RequestParam String questionId,
            @RequestBody DiscussionDto discussionDto
    ) {
        DiscussionDto discussionDto1 = discussionService.createDiscussion(userId, questionId, discussionDto);
        return new ResponseEntity<>(discussionDto1, HttpStatus.CREATED);
    }

    @GetMapping("/get_discussion")
    public ResponseEntity<DiscussionDto> getDiscussion(@RequestParam String discussionId) {
        DiscussionDto discussionDto1 = discussionService.getDiscussion(discussionId);
        return new ResponseEntity<>(discussionDto1, HttpStatus.OK);
    }

    @PutMapping("/update_discussion")
    public ResponseEntity<DiscussionDto> updateDiscussion(
            @RequestParam String userId,
            @RequestParam String discussionId,
            @RequestBody DiscussionDto discussionDto
    ) {
        DiscussionDto discussionDto1 = discussionService.updateDiscussion(userId, discussionId, discussionDto);
        return new ResponseEntity<>(discussionDto1, HttpStatus. OK);
    }

    @DeleteMapping("/delete_discussion")
    public ResponseEntity<ApiResponse> deleteDiscussion(@RequestParam String discussionId, @RequestParam String userId) {
        discussionService.deleteDiscussion(userId, discussionId);
        ApiResponse apiResponse = new ApiResponse("Discussion deleted successfully !!", true);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/get_discussions")
    public ResponseEntity<PagedDiscussionResponse> getDiscussions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String questionId,
            @RequestParam(defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(required = false) String searchQuery
    ) {
        PagedDiscussionResponse pagedDiscussionResponse = discussionService.getDiscussions(userId,
                questionId, searchQuery, pageNo, pageSize);
        return ResponseEntity.ok(pagedDiscussionResponse);
    }
}
