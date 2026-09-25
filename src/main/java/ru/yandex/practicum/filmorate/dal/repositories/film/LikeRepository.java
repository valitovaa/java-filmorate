package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LikeRepository {

    private static final String ADD_LIKE_QUERY = "MERGE INTO film_likes(film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private static final String ADD_MARK_QUERY = "MERGE INTO film_likes(film_id, user_id, score) VALUES (?, ?, ?)";

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

    //Найти пользователя с максимальным количеством пересечения по лайкам.
    private static final String FIND_MOST_SIMILAR_USER_QUERY = """
            SELECT fl.user_id
            FROM film_likes fl
            JOIN film_likes target
                ON fl.film_id = target.film_id
                AND fl.score = target.score
            WHERE target.user_id = ?
              AND fl.user_id <> ?
            GROUP BY fl.user_id
            ORDER BY COUNT(*) DESC
            LIMIT 1
            """;

    public Optional<Long> findMostSimilarUserId(long userId) {
        try {
            Long similarUserId = jdbc.queryForObject(
                    FIND_MOST_SIMILAR_USER_QUERY,
                    Long.class,
                    userId,
                    userId
            );
            return Optional.ofNullable(similarUserId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    //Определить фильмы, которые один пролайкал, а другой нет.

    //Рекомендовать фильмы, которым поставил лайк пользователь с похожими вкусами, а тот, для кого составляется рекомендация, ещё не поставил.
    private static final String FIND_RECOMMENDED_FILM_IDS_QUERY = """
                SELECT f.id
                FROM films f
                JOIN film_likes fl2 ON f.id = fl2.film_id AND fl2.user_id = ?
                LEFT JOIN film_likes fl1 ON f.id = fl1.film_id AND fl1.user_id = ?
                WHERE fl1.film_id IS NULL
                     AND EXISTS (
                         SELECT 1
                         FROM film_likes fl_avg
                         WHERE fl_avg.film_id = f.id
                         GROUP BY fl_avg.film_id
                         HAVING AVG(fl_avg.score) >= 6
                             OR AVG(fl_avg.score) >= 0
                     )
            """;

    public List<Long> findRecommendedFilmIds(long userId, long similarUserId) {
        return jdbc.queryForList(
                FIND_RECOMMENDED_FILM_IDS_QUERY,
                Long.class,
                similarUserId,
                userId
        );
    }

    public void addMark(Long filmId, Long userId, float score) {
        jdbc.update(ADD_MARK_QUERY, filmId, userId, score);
    }
}