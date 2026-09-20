package ru.yandex.practicum.filmorate.dal.repositories.film.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewLikeRepository {

    private final JdbcTemplate jdbc;

    public ReviewLikeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String ADD_USEFUL_QUERY = "MERGE INTO review_likes (review_id, user_id, is_useful) " +
            "KEY (review_id, user_id) VALUES (?, ?, ?)";

    private static final String DELETE_USEFUL_QUERY =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_useful = ?";

    public void addUseful(Long reviewId, Long userId, boolean isUseful) {
        jdbc.update(ADD_USEFUL_QUERY, reviewId, userId, isUseful);
    }

    public void deleteUseful(long reviewId, long userId, boolean isUseful) {
        jdbc.update(DELETE_USEFUL_QUERY, reviewId, userId, isUseful);
    }
}
