package ru.yandex.practicum.filmorate.dal.repositories.film.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_QUERY = "MERGE INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "KEY (user_id, film_id) VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";

    public static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, " +
            "film_id = ?, useful = ? WHERE review_id = ?";

    public static final String FIND_BY_USER_ID_AND_FILM_ID_QUERY =
            "SELECT * FROM reviews WHERE user_id = ? AND film_id = ?";

    public static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";

    public static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC";

    public static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC";

    public Review createReview(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    public Optional<Review> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Review updateReview(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getReviewId()
        );

        return review;
    }

    public Optional<Review> findByUserIdAndFilmId(Long userId, Long filmId) {
        return findOne(FIND_BY_USER_ID_AND_FILM_ID_QUERY, userId, filmId);
    }

    public void deleteByReviewId(Long id) {
        delete(DELETE_QUERY, id);
    }

    public Collection<Review> findReviewsByFilmId(Long filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public Collection<Review> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}
