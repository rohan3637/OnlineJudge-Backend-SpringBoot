package com.example.onlinejudge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedLeaderboardDto {

    private PageInfo pageInfo;
    private List<UserResponseDto> userResponseDtos;
}
