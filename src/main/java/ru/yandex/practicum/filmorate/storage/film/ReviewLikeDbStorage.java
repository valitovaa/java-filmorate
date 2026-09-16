package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.film.ReviewLikeRepository;

@Component("reviewLikeDbStorage")
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    public void addUseful(Long reviewId, Long userId, boolean isUseful) {
        reviewLikeRepository.addUseful(reviewId, userId, isUseful);
    }

    @Override
    public void deleteUseful(Long reviewId, Long userId) {
        reviewLikeRepository.deleteUseful(reviewId, userId);
    }

    @Override
    public long getCountLikes(Long reviewsId) {
        return reviewLikeRepository.getCountLikes(reviewsId);
    }

    @Override
    public long getCountDislikes(Long reviewsId) {
        return reviewLikeRepository.getCountDislikes(reviewsId);
    }

}
