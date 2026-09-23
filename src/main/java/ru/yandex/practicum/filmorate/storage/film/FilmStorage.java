package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    Collection<Film> findAll();

    Film postFilm(Film film);

    Film update(Film newFilm);

    Optional<Film> findFilmById(Long id);

    Collection<Film> getPopularFilms(Long count, Long genreId, Long year);

    void like(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> filmsByDirector(Long directorId, String sortBy);

    Collection<Film> getCommonFilmsByUsers(Long userId, Long friendId);
}