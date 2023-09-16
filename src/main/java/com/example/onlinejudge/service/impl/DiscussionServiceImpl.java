package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.exception.BadRequestException;
import com.example.onlinejudge.exception.ResourceNotFoundException;
import com.example.onlinejudge.models.Discussion;
import com.example.onlinejudge.models.Question;
import com.example.onlinejudge.models.User;
import com.example.onlinejudge.repository.DiscussionCustomRepository;
import com.example.onlinejudge.repository.DiscussionRepository;
import com.example.onlinejudge.repository.QuestionRepository;
import com.example.onlinejudge.repository.UserRepository;
import com.example.onlinejudge.service.DiscussionService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiscussionServiceImpl implements DiscussionService {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private DiscussionCustomRepository discussionCustomRepository;

    @Override
    public DiscussionDto createDiscussion(String userId, String questionId, DiscussionDto discussionDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        Discussion discussion = modelMapper.map(discussionDto, Discussion.class);
        discussion.setQuestion(questionOptional.get());
        discussion.setUser(userOptional.get());
        discussion.setTimeStamp(LocalDateTime.now());
        Discussion savedDiscussion = discussionRepository.save(discussion);
        DiscussionDto savedDiscussionDto = modelMapper.map(savedDiscussion, DiscussionDto.class);
        savedDiscussionDto.setUserResponseDto(modelMapper.map(savedDiscussion.getUser(), UserResponseDto.class));
        return savedDiscussionDto;
    }

    @Override
    public DiscussionDto getDiscussion(String discussionId) {
        Optional<Discussion> discussionOptional = discussionRepository.findById(discussionId);
        if(discussionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Discusion", "id", discussionId);
        }
        DiscussionDto discussionDto = modelMapper.map(discussionOptional.get(), DiscussionDto.class);
        discussionDto.setUserResponseDto(modelMapper.map(discussionOptional.get().getUser(), UserResponseDto.class));
        discussionDto.setQuestionResponseDto(modelMapper.map(discussionOptional.get().getQuestion(), QuestionResponseDto.class));
        return discussionDto;
    }

    @Override
    public DiscussionDto updateDiscussion(String userId, String discussionId, DiscussionDto discussionDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Optional<Discussion> discussionOptional = discussionRepository.findById(discussionId);
        if(discussionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Discussion", "id", discussionId);
        }
        if(discussionOptional.get().getUser() != userOptional.get()) {
            throw new BadRequestException("Only owner can delete their comment !!");
        }
        Discussion discussion = discussionOptional.get();
        discussion.setTitle(discussionDto.getTitle());
        discussion.setComment(discussionDto.getComment());
        discussion.setTimeStamp(LocalDateTime.now());
        Discussion updatedDiscussion = discussionRepository.save(discussion);
        DiscussionDto updatedDiscussionDto = modelMapper.map(updatedDiscussion, DiscussionDto.class);
        updatedDiscussionDto.setUserResponseDto(modelMapper.map(updatedDiscussion.getUser(), UserResponseDto.class));
        updatedDiscussionDto.setQuestionResponseDto(modelMapper.map(updatedDiscussion.getQuestion(), QuestionResponseDto.class));
        return updatedDiscussionDto;
    }

    @Override
    public void deleteDiscussion(String userId, String discussionId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Optional<Discussion> discussionOptional = discussionRepository.findById(discussionId);
        if(discussionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Discussion", "id", discussionId);
        }
        if(discussionOptional.get().getUser() != userOptional.get()) {
            throw new BadRequestException("Only owner can delete their comment !!");
        }
        discussionRepository.delete(discussionOptional.get());
    }

    @Override
    public PagedDiscussionResponse getDiscussions(String userId, String questionId, String searchQuery, Integer pageNo, Integer pageSize) {
        List<Discussion> discussions = discussionCustomRepository.getDiscussionBySearch(
                userId, questionId, searchQuery, pageNo, pageSize);
        Integer totalCount = discussionCustomRepository.getDiscussionCount(userId, questionId, searchQuery);
        List<DiscussionDto> discussionDtos = new ArrayList<>();
        discussions.forEach(discussion -> {
            DiscussionDto discussionDto = modelMapper.map(discussion, DiscussionDto.class);
            if(questionId == null) discussionDto.setQuestionResponseDto(modelMapper.map(discussion.getQuestion(), QuestionResponseDto.class));
            if(userId == null) discussionDto.setUserResponseDto(modelMapper.map(discussion.getUser(), UserResponseDto.class));
            discussionDtos.add(discussionDto);
        });
        PageInfo pageInfo = new PageInfo(pageNo, pageSize, totalCount);
        return new PagedDiscussionResponse(pageInfo, discussionDtos);
    }
}
