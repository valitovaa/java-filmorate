package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review postReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(Long id);

    Optional<Review> getReviewById(Long id);

    Collection<Review> getAllReviewsByFilmId(Long id);

    Collection<Review> getAllReviews();

    Optional<Review> findReviewByUserIdAndFilmId(Long userId, Long filmId);
}
