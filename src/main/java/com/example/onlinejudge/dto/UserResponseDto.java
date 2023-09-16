package com.example.onlinejudge.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

@Data
public class UserResponseDto {

    private String userId;
    private String name;
    private String email;
    private Integer score;
}
