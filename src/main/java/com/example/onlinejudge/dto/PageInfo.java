package com.example.onlinejudge.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageInfo {

    private Integer pageNo;
    private Integer pageSize;
    private Integer resultCount;
}
