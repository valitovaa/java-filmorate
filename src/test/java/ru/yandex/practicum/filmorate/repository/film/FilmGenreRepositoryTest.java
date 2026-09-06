package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.dal.mappers.film.GenreRowMapper;
import ru.yandex.practicum.dal.repositories.film.FilmGenreRepository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmGenreRepository.class,
        GenreRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenreRepositoryTest {

    private final FilmGenreRepository filmGenreRepository;
    private final JdbcTemplate jdbc;

    @Test
    void addGenre_shouldAddGenreToFilm() {
        insertFilm(1L);

        filmGenreRepository.addGenre(1L, 1L);

        List<Genre> genres =
                filmGenreRepository.findGenresByFilmId(1L);

        assertThat(genres)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void findGenresByFilmId_shouldReturnFilmGenres() {
        insertFilm(1L);

        filmGenreRepository.addGenre(1L, 1L);
        filmGenreRepository.addGenre(1L, 2L);
        filmGenreRepository.addGenre(1L, 6L);

        List<Genre> genres =
                filmGenreRepository.findGenresByFilmId(1L);

        assertThat(genres)
                .hasSize(3)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L, 6L);
    }

    @Test
    void deleteGenres_shouldDeleteAllGenresOfFilm() {
        insertFilm(1L);

        filmGenreRepository.addGenre(1L, 1L);
        filmGenreRepository.addGenre(1L, 2L);

        filmGenreRepository.deleteGenres(1L);

        List<Genre> genres =
                filmGenreRepository.findGenresByFilmId(1L);

        assertThat(genres).isEmpty();
    }

    private void insertFilm(Long id) {
        jdbc.update(
                """
                INSERT INTO films
                    (id, name, description, release_date, duration, mpa)
                VALUES
                    (?, ?, ?, ?, ?, ?)
                """,
                id,
                "Тестовый фильм",
                "Описание",
                "2000-01-01",
                120,
                "G"
        );
    }
}