package com.example.onlinejudge.repository;

import com.example.onlinejudge.models.Question;
import com.example.onlinejudge.models.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionCustomRepository {

    @Autowired
    private EntityManager entityManager;

    public List<Question> getQuestionByFilters(String userId, List<String> topics, List<String> difficulties, String searchQuery, Integer pageNo, Integer pageSize) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
        Root<Question> root = criteriaQuery.from(Question.class);

        criteriaQuery.select(root).distinct(true)
                .where(getPredicates(root, criteriaBuilder, userId, topics, difficulties, searchQuery));

        final TypedQuery<Question> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNo - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);
        return typedQuery.getResultList();
    }

    public Integer getCountByFilters(String userId, List<String> topics, List<String> difficulties, String searchQuery) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Question> countRoot = countQuery.from(Question.class);

        countQuery.select(criteriaBuilder.countDistinct(countRoot))
                .where(criteriaBuilder.and(getPredicates(countRoot, criteriaBuilder, userId, topics, difficulties, searchQuery)));

        return Math.toIntExact(entityManager.createQuery(countQuery).getSingleResult());
    }

    private Predicate[] getPredicates(Root<Question> root, CriteriaBuilder criteriaBuilder, String userId,
                List<String> topics, List<String> difficulties, String searchQuery) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate defaultPredicate = criteriaBuilder.isNotNull(root.get("questionId"));
        predicates.add(defaultPredicate);
        if(userId != null) {
            Predicate ownerPredicate = criteriaBuilder.equal(root.get("author"), userId);
            predicates.add(ownerPredicate);
        }
        if(topics != null) {
            Join<Question, Topic> topicsJoin = root.join("topics", JoinType.LEFT);
            Predicate topicPredicate = topicsJoin.get("topicName").in(topics);
            predicates.add(topicPredicate);
        }
        if(difficulties != null) {
            Predicate difficultyPredicate = root.get("difficulty").in(difficulties);
            predicates.add(difficultyPredicate);
        }
        if (searchQuery != null) {
            Predicate titleSearchPredicate = criteriaBuilder.like(root.get("title"), "%" + searchQuery + "%");
            Predicate descriptionSearchPredicate = criteriaBuilder.like(root.get("description"), "%" + searchQuery + "%");

            Predicate searchPredicate = criteriaBuilder.or(titleSearchPredicate, descriptionSearchPredicate);
            predicates.add(searchPredicate);
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
