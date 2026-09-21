package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.postFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @Min(value = 1, message = "Количество фильмов должно быть не отрицательным числом")
            @RequestParam(defaultValue = "10")
            int count,

            @Positive(message = "Id жанра должно быть числом положительным")
            @RequestParam(required = false)
             Long genreId,

            @Min(value = 1895, message = "Год выхода фильма на экран не может быть ранее 1895")
            @RequestParam(required = false)
            Long year

    ) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }


    @GetMapping("/common")
    public Collection<Film> getCommonFilmsByUsers(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilmsByUsers(userId, friendId);
    }

    /*@GetMapping("/popular")
    public List<Film> getMostPopularsFilm(
            @Min(value = 1, message = "Количество фильмов должно быть не отрицательным числом")
            @RequestParam(defaultValue = "10")
            int count,

            @Positive(message = "Id жанра должно быть числом положительным")
            @RequestParam(required = false)
            Long genreId,

            @Min(value = 1895, message = "Год выхода фильма на экран не может быть ранее 1895")
            @RequestParam(required = false)
            Long year
    ) {
        return filmService.getMostPopularsFilm(count, genreId, year);
    }*/
}