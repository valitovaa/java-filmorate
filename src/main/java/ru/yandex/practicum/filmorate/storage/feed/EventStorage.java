package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface EventStorage {

    List<Event> findByUserId(Long userId);

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    void addReview(Long userId, Long filmId);

    void removeReview(Long userId, Long filmId);

    void updateReview(Long userId, Long filmId);

}