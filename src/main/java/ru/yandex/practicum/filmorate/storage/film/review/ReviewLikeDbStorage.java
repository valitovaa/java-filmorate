package ru.yandex.practicum.filmorate.storage.film.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.film.review.ReviewLikeRepository;

@Component("reviewLikeDbStorage")
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    public void addUseful(Long reviewId, Long userId, boolean isUseful) {
        reviewLikeRepository.addUseful(reviewId, userId, isUseful);
    }

    @Override
    public void deleteUseful(Long reviewId, Long userId, boolean isUseful) {
        reviewLikeRepository.deleteUseful(reviewId, userId, isUseful);
    }
}