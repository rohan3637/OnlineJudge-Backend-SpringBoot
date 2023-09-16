package com.example.onlinejudge.repository;

import com.example.onlinejudge.models.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, String> {
}
