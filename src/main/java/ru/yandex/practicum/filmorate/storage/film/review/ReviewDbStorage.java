package ru.yandex.practicum.filmorate.storage.film.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.film.review.ReviewRepository;
import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.List;
import java.util.Optional;

@Component("reviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final ReviewRepository reviewRepository;

    @Override
    public Review postReview(Review review) {
        return reviewRepository.createReview(review);
    }

    @Override
    public void updateReview(Review review) {
        reviewRepository.updateReview(review);
    }

    @Override
    public void deleteReviewById(Long id) {
        reviewRepository.deleteByReviewId(id);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Long filmId) {
        return reviewRepository.findReviewsByFilmId(filmId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> findReviewByUserIdAndFilmId(Long userId, Long filmId) {
        return reviewRepository.findByUserIdAndFilmId(userId, filmId);
    }
}
