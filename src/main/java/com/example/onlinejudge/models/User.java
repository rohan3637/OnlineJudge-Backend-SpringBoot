package com.example.onlinejudge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @OneToMany(targetEntity = Question.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    @JsonIgnore
    private List<Question> questions;

    @OneToMany(targetEntity = Submission.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    private List<Submission> submissions;

    @OneToMany(targetEntity = Discussion.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Discussion> discussions;

    @Column(name = "score")
    private Integer score;
}
