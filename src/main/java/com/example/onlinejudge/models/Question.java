package com.example.onlinejudge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String questionId;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "topic_mapping", joinColumns = @JoinColumn(name = "question", referencedColumnName = "questionId"), inverseJoinColumns = @JoinColumn(name = "topic", referencedColumnName = "id"))
    private List<Topic> topics;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT", length = 4000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @Column(name = "total_submission")
    private Integer totalSubmission;

    @Column(name = "correct_submission")
    private Integer correctSubmission;

    @Column(name = "author_solution")
    private String authorSolution;

    @Column(name = "hints")
    private String hints;

    @OneToMany(targetEntity = Submission.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question")
    @JsonIgnore
    private List<Submission> submissions;

    @OneToMany(targetEntity = TestCase.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question")
    @JsonIgnore
    private List<TestCase> testCases;

    @OneToMany(targetEntity = Discussion.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "question")
    @JsonIgnore
    private List<Discussion> discussions;
}
