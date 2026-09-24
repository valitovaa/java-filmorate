package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.storage.feed.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.review.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final EventStorage eventStorage;

    public ReviewService(
            @Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("reviewLikeDbStorage") ReviewLikeStorage reviewLikeStorage,
            @Qualifier("eventDbStorage") EventStorage eventStorage

    ) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewLikeStorage = reviewLikeStorage;
        this.eventStorage = eventStorage;
    }

    public Review postReview(NewReviewRequest newReview) {
        findFilmById(newReview.getFilmId());
        findUserById(newReview.getUserId());
        Review review = reviewStorage.postReview(ReviewMapper.mapToReview(newReview));
        eventStorage.addReviewEvent(review.getUserId(), review.getReviewId());
        return review;
    }

    public Review updateReview(UpdateReviewRequest updateReview) {
        Review review = findReviewById(updateReview.getReviewId());
        ReviewMapper.updateReviewFields(review, updateReview);

        Optional<Review> otherReview =
                reviewStorage.findReviewByUserIdAndFilmId(
                        review.getUserId(),
                        review.getFilmId()
                );

        //Пара user_id и film_id уникальная для таблицы reviews. Проверяем наличие такой пары в другом отзыве
        otherReview.ifPresent(preview -> {
            if (!preview.getReviewId().equals(review.getReviewId())) {
                String message = String.format(
                        "Пользователь с id=%s уже оставлял отзыв к фильму с id=%s под id=%s",
                        review.getUserId(),
                        review.getFilmId(),
                        preview.getReviewId()
                );
                throw new ConditionsNotMetException(message);
            }
        });

        reviewStorage.updateReview(review);
        eventStorage.updateReviewEvent(review.getUserId(), review.getReviewId());

        return review;
    }

    public void deleteReview(Long id) {
        Optional<Review> review = reviewStorage.getReviewById(id);
        if (review.isPresent()) {
            reviewStorage.deleteReviewById(id);
            eventStorage.removeReviewEvent(review.get().getUserId(), review.get().getReviewId());
        }

    }

    public Review getReviewById(Long id) {
        return findReviewById(id);
    }

    public List<Review> getAllReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.getAllReviews(count);
        }

        findFilmById(filmId);
        return reviewStorage.getAllReviewsByFilmId(filmId, count);
    }

    public Review addDislikeReview(Long reviewId, Long userId) {
        findUserById(userId);
        Review review = findReviewById(reviewId);
        reviewLikeStorage.addUseful(reviewId, userId, false);
        return review;
    }

    public Review addLikeReview(Long reviewId, Long userId) {
        findUserById(userId);
        Review review = findReviewById(reviewId);
        reviewLikeStorage.addUseful(reviewId, userId, true);
        return review;
    }

    public Review deleteLikeReview(Long reviewId, Long userId) {
        findUserById(userId);
        Review review = findReviewById(reviewId);
        reviewLikeStorage.deleteUseful(reviewId, userId, true);
        return review;
    }

    public Review deleteDislikeReview(Long reviewId, Long userId) {
        findUserById(userId);
        Review review = findReviewById(reviewId);
        reviewLikeStorage.deleteUseful(reviewId, userId, false);
        return review;
    }

    private void findFilmById(Long id) {
        filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден, id=" + id));
    }

    private void findUserById(Long id) {
        userStorage.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден, id=" + id));
    }

    private Review findReviewById(Long id) {
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден, id=" + id));
    }
}
