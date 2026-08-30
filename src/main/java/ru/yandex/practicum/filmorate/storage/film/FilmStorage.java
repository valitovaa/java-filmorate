package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film postFilm(Film film);

    Film update(Film newFilm);

    Optional<Film> findFilmById(Long id);

    Collection<Film> getPopularFilms(int count);

    void like(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

}
