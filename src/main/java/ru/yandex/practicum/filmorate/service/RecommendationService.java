package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
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
        log.warn("In service user is found. UserId={}",userId);
        List<Film> films = recommendationStorage.getRecommendedFilms(userId);
        log.warn("In service user is found. getRecommendedFilms on recommendationStorage complete. Films={}", films);
        return recommendationStorage.getRecommendedFilms(userId);
    }
}