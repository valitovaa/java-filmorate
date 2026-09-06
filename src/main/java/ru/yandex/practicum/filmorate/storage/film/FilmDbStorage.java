package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dal.repositories.film.FilmGenreRepository;
import ru.yandex.practicum.dal.repositories.film.FilmRepository;
import ru.yandex.practicum.dal.repositories.film.LikeRepository;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;

    public FilmDbStorage(
            FilmRepository filmRepository,
            FilmGenreRepository filmGenreRepository,
            LikeRepository likeRepository
    ) {
        this.filmRepository = filmRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.likeRepository = likeRepository;
    }

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
        // здесь добавим специальный запрос в FilmRepository
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