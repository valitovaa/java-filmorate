package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface EventStorage {

    List<Event> findByUserId(Long userId);

    void addLikeEvent(Long userId, Long filmId);

    void removeLikeEvent(Long userId, Long filmId);

    void addFriendEvent(Long userId, Long friendId);

    void removeFriendEvent(Long userId, Long friendId);

    void addReviewEvent(Long userId, Long reviewId);

    void removeReviewEvent(Long userId, Long reviewId);

    void updateReviewEvent(Long userId, Long reviewId);

}