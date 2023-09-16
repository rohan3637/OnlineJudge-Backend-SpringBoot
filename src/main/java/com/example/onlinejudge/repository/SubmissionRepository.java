package com.example.onlinejudge.repository;

import com.example.onlinejudge.models.Langauge;
import com.example.onlinejudge.models.Question;
import com.example.onlinejudge.models.Submission;
import com.example.onlinejudge.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findByUserAndQuestion(User user, Question question);
}
