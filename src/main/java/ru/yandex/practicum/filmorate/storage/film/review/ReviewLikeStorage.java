package ru.yandex.practicum.filmorate.storage.film.review;

public interface ReviewLikeStorage {

    void addUseful(Long reviewId, Long userId, boolean isUseful);

    void deleteUseful(Long reviewId, Long userId, boolean isUseful);
}