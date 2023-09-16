package com.example.onlinejudge.repository;

import com.example.onlinejudge.models.Discussion;
import com.example.onlinejudge.models.Submission;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubmissionCustomRepository {

    @Autowired
    private EntityManager entityManager;

    public List<Submission> getSubmissionByFilter(String userId, String questionId,
               String status, List<String> languages, Integer pageNo, Integer pageSize) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Submission> criteriaQuery = criteriaBuilder.createQuery(Submission.class);
        Root<Submission> root = criteriaQuery.from(Submission.class);

        criteriaQuery.select(root).distinct(true)
                .where(getPredicates(root, criteriaBuilder, userId, questionId, status, languages));

        final TypedQuery<Submission> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNo - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);
        return typedQuery.getResultList();
    }

    public Integer getSubmissionCount(String userId, String questionId, String status, List<String> languages) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Submission> countRoot = countQuery.from(Submission.class);

        countQuery.select(criteriaBuilder.countDistinct(countRoot))
                .where(criteriaBuilder.and(getPredicates(countRoot, criteriaBuilder, userId, questionId, status, languages)));

        return Math.toIntExact(entityManager.createQuery(countQuery).getSingleResult());
    }

    private Predicate[] getPredicates(Root<Submission> root, CriteriaBuilder criteriaBuilder, String userId,
                 String questionId, String status, List<String> languages) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate defaultPredicate = criteriaBuilder.isNotNull(root.get("submissionId"));
        predicates.add(defaultPredicate);
        if(userId != null) {
            Predicate ownerPredicate = criteriaBuilder.equal(root.get("user"), userId);
            predicates.add(ownerPredicate);
        }
        if(questionId != null) {
            Predicate questionPredicate = criteriaBuilder.equal(root.get("question"), questionId);
            predicates.add(questionPredicate);
        }
        if (status != null) {
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), status);
            predicates.add(statusPredicate);
        }
        if(languages != null) {
            Predicate languagePredicate = root.get("language").in(languages);
            predicates.add(languagePredicate);
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
