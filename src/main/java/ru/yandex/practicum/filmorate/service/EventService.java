package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.storage.feed.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public EventService(
            EventStorage eventStorage,
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public List<Event> getEvents(Long userId) {
        userStorage.findUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return eventStorage.findByUserId(userId);
    }
}