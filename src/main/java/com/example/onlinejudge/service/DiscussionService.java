package com.example.onlinejudge.service;

import com.example.onlinejudge.dto.DiscussionDto;
import com.example.onlinejudge.dto.PagedDiscussionResponse;

public interface DiscussionService {

    DiscussionDto createDiscussion(String userId, String questionId, DiscussionDto discussionDto);
    DiscussionDto getDiscussion(String discussionId);
    DiscussionDto updateDiscussion(String userId, String discussionId, DiscussionDto discussionDto);
    void deleteDiscussion(String userId, String discussionId);
    public PagedDiscussionResponse getDiscussions(String userId, String questionId,
             String searchQuery, Integer pageNo, Integer pageSize);

}
