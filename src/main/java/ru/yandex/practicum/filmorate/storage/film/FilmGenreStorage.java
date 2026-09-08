package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Set;

public interface FilmGenreStorage {

    Set<Genre> getGenres(Long filmId);

    void addGenre(Long filmId, Long genreId);

    void deleteGenres(Long filmId);
}