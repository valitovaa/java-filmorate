package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final Map<Long, Set<Long>> likes = new HashMap<>();

    public void like(Long userId, Long filmId) {
        findFilmOrThrow(filmId);
        findUserOrThrow(userId);

        likes.computeIfAbsent(filmId, id -> new HashSet<>()).add(userId);

    }

    public void removeLike(Long userId, Long filmId) {
        findFilmOrThrow(filmId);
        findUserOrThrow(userId);

        Set<Long> filmLikes = likes.get(filmId);

        if (filmLikes != null) {
            filmLikes.remove(userId);
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream().sorted(Comparator.comparingInt((Film film) -> likes.getOrDefault(film.getId(), Set.of()).size()).reversed()).limit(count).toList();
    }


    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }


    public Film postFilm(Film film) {
        return filmStorage.postFilm(film);
    }


    public Film update(Film film) {
        return filmStorage.update(film);
    }

    private void findUserOrThrow(Long id) {
        userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    private void findFilmOrThrow(Long id) {
        filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }
}


