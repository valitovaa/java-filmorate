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

    public ReviewService(
            @Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("reviewLikeDbStorage") ReviewLikeStorage reviewLikeStorage

    ) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewLikeStorage = reviewLikeStorage;
    }

    public Review postReview(NewReviewRequest newReview) {
        findFilmById(newReview.getFilmId());
        findUserById(newReview.getUserId());
        return reviewStorage.postReview(ReviewMapper.mapToReview(newReview));
    }

    public Review updateReview(UpdateReviewRequest updateReview) {
        Review review = findReviewById(updateReview.getReviewId());
        if (updateReview.hasFilmId()) {
            findFilmById(updateReview.getFilmId());
        }
        if (updateReview.hasUserId()) {
            findUserById(updateReview.getUserId());
        }
        ReviewMapper.updateReviewFields(review, updateReview);

        //Пара user_id и film_id уникальная для таблицы reviews. Проверяем наличие такой пары в другом отзыве
        Optional<Review> otherReview = reviewStorage.findReviewByUserIdAndFilmId(review.getUserId(), review.getFilmId());
        if (otherReview.isPresent()) {
            if (!(otherReview.get().getReviewId().equals(review.getReviewId()))) {
                String message = String.format(
                        "Пользовать с id=%s уже оставлял отзыв к фильму с id=%s под id=%s",
                        review.getUserId(), review.getFilmId(), otherReview.get().getReviewId()
                );
                throw new ConditionsNotMetException(message);
            }
        }
        reviewStorage.updateReview(review);
        return review;
    }

    public void deleteReview(Long id) {
        reviewStorage.deleteReviewById(id);
    }

    public Review getReviewById(Long id) {
        return findReviewById(id);
    }

    public List<Review> getAllReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.getAllReviews().stream().limit(count).toList();
        } else {
            findFilmById(filmId);
            return reviewStorage.getAllReviewsByFilmId(filmId).stream().limit(count).toList();
        }
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
