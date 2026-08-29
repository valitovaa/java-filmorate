package ru.yandex.practicum.filmorate.storage.film;


import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film postFilm(Film film) {

        film.setId(getNextId());

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        Film oldFilm = films.get(newFilm.getId());

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        return oldFilm;
    }

    @Override
    public void like(Long filmId, Long userId) {

        likes.computeIfAbsent(filmId, id -> new HashSet<>()).add(userId);

    }

    @Override
    public void removeLike(Long filmId, Long userId) {

        Set<Long> filmLikes = likes.get(filmId);

        if (filmLikes != null) {
            filmLikes.remove(userId);
        }
    }


    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    public Collection<Film> getPopularFilms(int count) {
        return films.values().stream().sorted(Comparator.comparingInt((Film film) -> likes.getOrDefault(film.getId(), Set.of()).size()).reversed()).limit(count).toList();
    }


    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);

        return ++currentMaxId;
    }

}
