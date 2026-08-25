package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {

        validateReleaseDate(film);
        validateDuration(film);

        film.setId(getNextId());

        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        validateReleaseDate(newFilm);
        validateDuration(newFilm);

        Film oldFilm = films.get(newFilm.getId());

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);

        return ++currentMaxId;
    }

    private void validateReleaseDate(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateDuration(Film film) {
        if (film.getDuration() <= 0) {
            throw new ConditionsNotMetException(
                    "Продолжительность должна быть положительной"
            );
        }
    }
}