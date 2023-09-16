package com.example.onlinejudge.repository;

import com.example.onlinejudge.models.Discussion;
import com.example.onlinejudge.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, String> {

    List<Discussion> findByQuestion(Question question, Pageable pageable);
}
