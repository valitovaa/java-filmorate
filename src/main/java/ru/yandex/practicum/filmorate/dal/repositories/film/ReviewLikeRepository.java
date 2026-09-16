package ru.yandex.practicum.filmorate.dal.repositories.film;

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

    private static final String GET_COUNT_LIKES_QUERY =
            "SELECT COUNT (*) FROM review_likes WHERE review_id = ? AND is_useful = true";

    private static final String GET_COUNT_DISLIKES_QUERY =
            "SELECT COUNT (*) FROM review_likes WHERE review_id = ? AND is_useful = false";

    private static final String DELETE_USEFUL_QUERY = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";

    public void addUseful(Long reviewId, Long userId, boolean isUseful) {
        jdbc.update(ADD_USEFUL_QUERY, reviewId, userId, isUseful);
    }

    public int getCountLikes(long reviewId) {
        Integer count = jdbc.queryForObject(GET_COUNT_LIKES_QUERY, Integer.class, reviewId);
        return count != null ? count : 0;
    }

    public int getCountDislikes(long reviewId) {
        Integer count = jdbc.queryForObject(GET_COUNT_DISLIKES_QUERY, Integer.class, reviewId);
        return count != null ? count : 0;
    }

    public void deleteUseful(long reviewId, long userId) {
        jdbc.update(DELETE_USEFUL_QUERY, reviewId, userId);
    }
}
