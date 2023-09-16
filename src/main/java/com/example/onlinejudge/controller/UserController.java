package com.example.onlinejudge.controller;

import com.example.onlinejudge.dto.*;
import com.example.onlinejudge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register_user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody CreateUserDto createUserDto) {
        userService.registerUser(createUserDto);
        ApiResponse apiResponse = new ApiResponse("User regsitered successfully !!", true);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/get_user_details")
    public ResponseEntity<ProfileDto> getUserDetails(@RequestParam String userId) {
        ProfileDto user = userService.getUserDetails(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get_leaderboard")
    public ResponseEntity<PagedLeaderboardDto> getLeaderboard(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                        @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        PagedLeaderboardDto userResponseDtos = userService.getLeaderboard(pageNo, pageSize);
        return new ResponseEntity<>(userResponseDtos, HttpStatus.OK);
    }

    @PutMapping("/update_profile")
    public ResponseEntity<UserResponseDto> updateUserDetails(@RequestParam String userId, @RequestBody CreateUserDto createUserDto) {
        UserResponseDto userResponseDto = userService.updateUserDetails(userId, createUserDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }
}
