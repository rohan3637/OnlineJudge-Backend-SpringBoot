package com.example.onlinejudge.repository;

import com.example.onlinejudge.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    @Query(value = "SELECT usr.user_id, usr.name, usr.email, SUM( " +
            "CASE WHEN q.difficulty = 'ESAY' THEN 10 " +
            "WHEN q.difficulty = 'MEDIUM' THEN 20 " +
            "WHEN q.difficulty = 'HARD' THEN 30 " +
            "ELSE 0 END) AS score " +
            "FROM onlinejudge.user AS usr " +
            "LEFT JOIN onlinejudge.submission AS sub ON usr.user_id = sub.user_id " +
            "LEFT JOIN onlinejudge.question AS q ON q.question_id = sub.question_id " +
            "WHERE sub.status = 'ACCEPTED' " +
            "GROUP BY usr.user_id " +
            "ORDER BY score DESC " +
            "LIMIT :pageSize OFFSET :offset ", nativeQuery = true)
    List<Object[]> findUsersWithScores(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    @Query(value = "SELECT COUNT(DISTINCT usr.user_id) " +
            "FROM onlinejudge.user AS usr " +
            "LEFT JOIN onlinejudge.submission AS sub ON usr.user_id = sub.user_id " +
            "LEFT JOIN onlinejudge.question AS q ON q.question_id = sub.question_id " +
            "WHERE sub.status = 'ACCEPTED' ", nativeQuery = true)
    Integer countUsersWithScores();

    @Query(value = "SELECT * from user ORDER BY score desc LIMIT :pageSize OFFSET :offset ", nativeQuery = true)
    List<User> findAllOrderByScoreDesc(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

}
