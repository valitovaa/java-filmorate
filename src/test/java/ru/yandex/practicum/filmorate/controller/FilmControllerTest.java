package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void findAll_whenNoFilms_returnsEmptyCollection() {
        Collection<Film> films = filmController.findAll();

        assertTrue(films.isEmpty());
    }

    @Test
    void postFilm_whenFilmIsValid_addsFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Фильм о виртуальной реальности");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(Duration.ofMinutes(136));

        Film result = filmController.postFilm(film);

        assertEquals(1L, result.getId());
        assertEquals("Матрица", result.getName());
        assertEquals("Фильм о виртуальной реальности", result.getDescription());
        assertEquals(LocalDate.of(1999, 3, 31), result.getReleaseDate());
        assertEquals(Duration.ofMinutes(136), result.getDuration());
    }

    @Test
    void postFilm_whenReleaseDateIsBeforeMinimum_throwsException() {
        Film film = new Film();
        film.setName("Первый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(Duration.ofMinutes(100));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> filmController.postFilm(film));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void postFilm_whenReleaseDateEqualsMinimum_addsFilm() {
        Film film = new Film();
        film.setName("Первый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(Duration.ofMinutes(50));

        Film result = filmController.postFilm(film);

        assertEquals(1L, result.getId());
    }

    @Test
    void postFilm_whenSeveralFilms_generatesDifferentIds() {
        Film firstFilm = new Film();
        firstFilm.setName("Матрица");
        firstFilm.setDescription("Описание");
        firstFilm.setReleaseDate(LocalDate.of(1999, 3, 31));
        firstFilm.setDuration(Duration.ofMinutes(136));

        Film secondFilm = new Film();
        secondFilm.setName("Интерстеллар");
        secondFilm.setDescription("Описание");
        secondFilm.setReleaseDate(LocalDate.of(2014, 10, 26));
        secondFilm.setDuration(Duration.ofMinutes(169));

        Film firstResult = filmController.postFilm(firstFilm);
        Film secondResult = filmController.postFilm(secondFilm);

        assertEquals(1L, firstResult.getId());
        assertEquals(2L, secondResult.getId());
    }

    @Test
    void findAll_whenFilmsExist_returnsAllFilms() {
        Film firstFilm = new Film();
        firstFilm.setName("Матрица");
        firstFilm.setDescription("Описание");
        firstFilm.setReleaseDate(LocalDate.of(1999, 3, 31));
        firstFilm.setDuration(Duration.ofMinutes(136));

        Film secondFilm = new Film();
        secondFilm.setName("Интерстеллар");
        secondFilm.setDescription("Описание");
        secondFilm.setReleaseDate(LocalDate.of(2014, 10, 26));
        secondFilm.setDuration(Duration.ofMinutes(169));

        filmController.postFilm(firstFilm);
        filmController.postFilm(secondFilm);

        Collection<Film> films = filmController.findAll();

        assertEquals(2, films.size());
        assertTrue(films.contains(firstFilm));
        assertTrue(films.contains(secondFilm));
    }

    @Test
    void update_whenFilmExists_updatesFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Старое описание");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(Duration.ofMinutes(136));

        Film savedFilm = filmController.postFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(savedFilm.getId());
        updatedFilm.setName("Матрица 2");
        updatedFilm.setDescription("Новое описание");
        updatedFilm.setReleaseDate(LocalDate.of(2003, 5, 15));
        updatedFilm.setDuration(Duration.ofMinutes(138));

        Film result = filmController.update(updatedFilm);

        assertEquals(savedFilm.getId(), result.getId());
        assertEquals("Матрица 2", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertEquals(LocalDate.of(2003, 5, 15), result.getReleaseDate());
        assertEquals(Duration.ofMinutes(138), result.getDuration());
    }

    @Test
    void update_whenIdIsNull_throwsException() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(Duration.ofMinutes(136));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> filmController.update(film));

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void update_whenFilmDoesNotExist_throwsException() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Матрица");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(Duration.ofMinutes(136));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.update(film));

        assertEquals("Фильм с id = 999 не найден", exception.getMessage());
    }

    @Test
    void update_whenReleaseDateIsBeforeMinimum_throwsException() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(Duration.ofMinutes(136));

        Film savedFilm = filmController.postFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(savedFilm.getId());
        updatedFilm.setName("Матрица");
        updatedFilm.setDescription("Новое описание");
        updatedFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        updatedFilm.setDuration(Duration.ofMinutes(136));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> filmController.update(updatedFilm));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }
}

