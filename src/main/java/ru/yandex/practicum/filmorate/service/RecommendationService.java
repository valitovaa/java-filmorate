package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class RecommendationService {

    private final UserStorage userStorage;
    private final RecommendationStorage recommendationStorage;

    public RecommendationService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("recommendationDbStorage")
            RecommendationStorage recommendationStorage
    ) {
        this.userStorage = userStorage;
        this.recommendationStorage = recommendationStorage;
    }

    public List<Film> getRecommendedFilms(Long userId) {
        userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Пользователь с id = " + userId + " не найден"
                        )
                );

        return recommendationStorage.getRecommendedFilms(userId);
    }
}