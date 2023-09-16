package com.example.onlinejudge.service.impl;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.exception.BadRequestException;
import com.example.onlinejudge.exception.ResourceNotFoundException;
import com.example.onlinejudge.models.*;
import com.example.onlinejudge.repository.UserRepository;
import com.example.onlinejudge.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void registerUser(CreateUserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail());
        if(user != null) {
            log.error("User already exists with this email !!");
            throw new BadRequestException("User already exists with this email.");
        }
        User newUser = modelMapper.map(userDto, User.class);
        newUser.setRole("ROLE_USER");
        userRepository.save(newUser);
    }

    @Override
    public ProfileDto getUserDetails(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Integer easySolved = 0, mediumSolved = 0, hardSolved = 0;
        List<Submission> submissionList = userOptional.get().getSubmissions();
        Set<String> seen = new HashSet<>();
        for(Submission submission : submissionList) {
            Question question = submission.getQuestion();
            if(submission.getStatus() == Status.ACCEPTED) {
                if(!seen.contains(question.getQuestionId())) {
                    seen.add(question.getQuestionId());
                    if (question.getDifficulty() == Difficulty.EASY) easySolved++;
                    else if (question.getDifficulty() == Difficulty.MEDIUM) mediumSolved++;
                    else hardSolved++;
                }
            }
        }
        Integer totalSubmission = submissionList.size();
        Integer correctSubmission = easySolved + mediumSolved + hardSolved;
        Double accuracy = totalSubmission * 1.0 / totalSubmission;
        Integer totalPoints = easySolved * 10 + mediumSolved * 20 + hardSolved * 30;
        return new ProfileDto(userId, userOptional.get().getName(), userOptional.get().getEmail(),
                correctSubmission, easySolved, mediumSolved, hardSolved, accuracy, totalPoints);
    }

    @Override
    public UserResponseDto updateUserDetails(String userId, CreateUserDto createUserDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            log.error("User not found with this id !!");
            throw new ResourceNotFoundException("User", "id", userId);
        }
        User user = userOptional.get();
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        user.setPassword(createUserDto.getPassword());
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public PagedLeaderboardDto getLeaderboard(Integer pageNo, Integer pageSize) {
        Integer offset = (pageNo - 1) * pageSize;
        List<User> users = userRepository.findAllOrderByScoreDesc(pageSize, offset);
        List<UserResponseDto> userResponseDtos = users.parallelStream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
        Integer totalCount = Math.toIntExact(userRepository.count());
        PageInfo pageInfo = new PageInfo(pageNo, pageSize, totalCount);
        return new PagedLeaderboardDto(pageInfo, userResponseDtos);
    }
}
