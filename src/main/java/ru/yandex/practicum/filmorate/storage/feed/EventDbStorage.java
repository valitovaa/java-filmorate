package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.feed.EventRepository;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

import java.util.List;

@Component("eventDbStorage")
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final EventRepository eventRepository;

    @Override
    public void addLike(Long userId, Long filmId) {
        saveEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        saveEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        saveEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        saveEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    @Override
    public void addReview(Long userId, Long filmId) {
        saveEvent(userId, EventType.REVIEW, Operation.ADD, filmId);
    }

    @Override
    public void removeReview(Long userId, Long filmId) {
        saveEvent(userId, EventType.REVIEW, Operation.REMOVE, filmId);

    }

    @Override
    public void updateReview(Long userId, Long filmId) {
        saveEvent(userId, EventType.REVIEW, Operation.UPDATE, filmId);
    }

    @Override
    public List<Event> findByUserId(Long userId) {
        return eventRepository.findByUserId(userId);
    }

    private void saveEvent(
            Long userId,
            EventType eventType,
            Operation operation,
            Long entityId
    ) {
        Event event = new Event();

        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);

        eventRepository.addEvent(event);
    }
}