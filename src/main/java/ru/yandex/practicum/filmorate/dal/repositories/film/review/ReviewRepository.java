package ru.yandex.practicum.filmorate.dal.repositories.film.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_QUERY = "MERGE INTO reviews (content, is_positive, user_id, film_id) " +
            "KEY (user_id, film_id) VALUES (?, ?, ?, ?)";

    private static final String BASE_FIND_QUERY_PATH_ONE =
            "SELECT r.review_id, r.content, r.is_positive, r.user_id, r.film_id, " +
                    "COALESCE( SUM( CASE WHEN rl.is_useful = TRUE THEN 1 WHEN rl.is_useful = FALSE THEN -1 ELSE 0 END ), 0) " +
                    "AS useful " +
                    "FROM reviews r " +
                    "LEFT JOIN review_likes rl ON rl.review_id = r.review_id ";

    private static final String BASE_FIND_QUERY_PATH_TWO =
            "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id ORDER BY useful DESC";

    private static final String FIND_BY_ID_QUERY =
            BASE_FIND_QUERY_PATH_ONE + "WHERE r.review_id = ? " + BASE_FIND_QUERY_PATH_TWO;

    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, " +
            "film_id = ? WHERE review_id = ?";

    private static final String FIND_BY_USER_ID_AND_FILM_ID_QUERY =
            BASE_FIND_QUERY_PATH_ONE + "WHERE r.user_id = ? AND r.film_id = ? " + BASE_FIND_QUERY_PATH_TWO;

    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";

    private static final String FIND_BY_FILM_ID_QUERY =
            BASE_FIND_QUERY_PATH_ONE + "WHERE r.film_id = ? " + BASE_FIND_QUERY_PATH_TWO;

    private static final String FIND_ALL_QUERY = BASE_FIND_QUERY_PATH_ONE + BASE_FIND_QUERY_PATH_TWO;

    public Review createReview(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        review.setReviewId(id);
        return review;
    }

    public Optional<Review> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public void updateReview(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId()
        );
    }

    public Optional<Review> findByUserIdAndFilmId(Long userId, Long filmId) {
        return findOne(FIND_BY_USER_ID_AND_FILM_ID_QUERY, userId, filmId);
    }

    public void deleteByReviewId(Long id) {
        delete(DELETE_QUERY, id);
    }

    public List<Review> findReviewsByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public List<Review> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}
