package ru.yandex.practicum.filmorate.storage.film.review;

import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review postReview(Review review);

    void updateReview(Review review);

    void deleteReviewById(Long id);

    Optional<Review> getReviewById(Long id);

    List<Review> getAllReviews(int count);

    List<Review> getAllReviewsByFilmId(Long filmId, int count);

    Optional<Review> findReviewByUserIdAndFilmId(Long userId, Long filmId);
}
