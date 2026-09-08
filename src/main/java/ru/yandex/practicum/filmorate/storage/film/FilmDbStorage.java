package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.film.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repositories.film.LikeRepository;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Optional;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Film postFilm(Film film) {
        return filmRepository.addFilm(film);
    }

    @Override
    public Film update(Film film) {
        return filmRepository.update(film);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return filmRepository.findById(id);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return filmRepository.findPopularFilms(count);
    }

    @Override
    public void like(Long filmId, Long userId) {
        likeRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        likeRepository.deleteLike(filmId, userId);
    }
}