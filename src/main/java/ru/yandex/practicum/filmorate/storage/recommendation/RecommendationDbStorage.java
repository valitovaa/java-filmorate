package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.film.FilmRepository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.dal.repositories.film.LikeRepository;

import java.util.List;
import java.util.Optional;

@Component("recommendationDbStorage")
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {

    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;

    @Override
    public List<Film> getRecommendedFilms(Long userId) {
        Optional<Long> similarUserId = likeRepository.findMostSimilarUserId(userId);

        if (similarUserId.isEmpty()) {
            return List.of();
        }

        List<Long> filmIds = likeRepository.findRecommendedFilmIds(userId, similarUserId.get());

        if (filmIds.isEmpty()) {
            return List.of();
        }

        return filmRepository.findFilmsByIds(filmIds);
    }
}