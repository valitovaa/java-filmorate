package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repositories.film.FilmGenreRepository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Set;

@Component("filmGenreDbStorage")
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final FilmGenreRepository filmGenreRepository;

    @Override
    public Set<Genre> getGenres(Long filmId) {
        return filmGenreRepository.findGenresByFilmId(filmId);
    }

    @Override
    public void addGenre(Long filmId, Long genreId) {
        filmGenreRepository.addGenre(filmId, genreId);
    }

    @Override
    public void deleteGenres(Long filmId) {
        filmGenreRepository.deleteGenres(filmId);
    }
}