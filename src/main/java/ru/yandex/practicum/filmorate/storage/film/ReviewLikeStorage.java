package ru.yandex.practicum.filmorate.storage.film;

public interface ReviewLikeStorage {

    void addUseful(Long reviewId, Long userId, boolean isUseful);

    void deleteUseful(Long reviewId, Long userId);

    long getCountLikes(Long reviewsId);

    long getCountDislikes(Long reviewsId);
}
