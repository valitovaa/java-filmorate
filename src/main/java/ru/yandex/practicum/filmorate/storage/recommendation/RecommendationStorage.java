package ru.yandex.practicum.filmorate.storage.recommendation;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface RecommendationStorage {

    List<Film> getRecommendedFilms(Long userId);
}