package ru.yandex.practicum.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepository {

    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM film_likes WHERE film_id = ?";

    private final JdbcTemplate jdbc;

    public LikeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    public int countLikes(long filmId) {
        Integer count = jdbc.queryForObject(COUNT_LIKES_QUERY, Integer.class, filmId);

        return count != null ? count : 0;
    }
}