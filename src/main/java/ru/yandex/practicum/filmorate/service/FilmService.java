package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final UserStorage userStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("genreDbStorage") GenreStorage genreStorage,
            @Qualifier("filmGenreDbStorage") FilmGenreStorage filmGenreStorage,
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();

        for (Film film : films) {
            film.setGenres(filmGenreStorage.getGenres(film.getId()));
        }

        return films;
    }

    public Film findById(Long id) {
        Film film = findFilmOrThrow(id);
        film.setGenres(filmGenreStorage.getGenres(id));
        return film;
    }

    public Film postFilm(Film film) {
        validateReleaseDate(film);
        validateDuration(film);
        validateGenres(film.getGenres());

        Film savedFilm = filmStorage.postFilm(film);

        saveGenres(savedFilm.getId(), film.getGenres());

        return findById(savedFilm.getId());
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        validateDuration(film);

        findFilmOrThrow(film.getId());
        validateGenres(film.getGenres());

        Film updatedFilm = filmStorage.update(film);

        filmGenreStorage.deleteGenres(film.getId());
        saveGenres(film.getId(), film.getGenres());

        return findById(updatedFilm.getId());
    }

    public void like(Long filmId, Long userId) {
        findFilmOrThrow(filmId);
        findUserOrThrow(userId);

        filmStorage.like(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        findFilmOrThrow(filmId);
        findUserOrThrow(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Long count, Long genreId, Long year) {
        if (year != null) {
            Film film = new Film();
            film.setReleaseDate(LocalDate.of(year.intValue(), 1, 1));
            validateReleaseDate(film);
        }
        if (genreId != null) {
            Genre genre = new Genre();
            genre.setId(genreId);
            validateGenres(Set.of(genre));
        }
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> getCommonFilmsByUsers(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ConditionsNotMetException("Идентификаторы пользователей не могут быть равны");
        }

        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        Collection<Film> films = filmStorage.getCommonFilmsByUsers(userId, friendId);
        for (Film film : films) {
            film.setGenres(filmGenreStorage.getGenres(film.getId()));
        }

        return films;
    }

    private Film findFilmOrThrow(Long id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Фильм с id = " + id + " не найден"
                        )
                );
    }

    private void findUserOrThrow(Long id) {
        userStorage.findUserById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Пользователь с id = " + id + " не найден"
                        )
                );
    }

    private void validateGenres(Set<Genre> genres) {
        if (genres == null) {
            return;
        }

        for (Genre genre : genres) {
            genreStorage.findById(genre.getId())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Жанр с id = " + genre.getId() + " не найден"
                            )
                    );
        }
    }

    private void saveGenres(Long filmId, Set<Genre> genres) {
        if (genres == null) {
            return;
        }

        for (Genre genre : genres) {
            filmGenreStorage.addGenre(filmId, genre.getId());
        }
    }

    private void validateReleaseDate(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ConditionsNotMetException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года"
            );
        }
    }

    private void validateDuration(Film film) {
        if (film.getDuration() <= 0) {
            throw new ConditionsNotMetException(
                    "Продолжительность должна быть положительной"
            );
        }
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if (directorId == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (sortBy == null || sortBy.isBlank()) {
            throw new ValidationException("Сортировка должна быть указана");
        }

        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new ValidationException("Сортировка должна быть по лайкам либо годам");
        }

        return filmStorage.filmsByDirector(directorId, sortBy);
    }
}