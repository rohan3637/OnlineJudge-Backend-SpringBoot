package com.example.onlinejudge.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "topic_name", nullable = false)
    private String topicName;
}
