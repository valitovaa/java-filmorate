package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.dal.mappers.film.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.repositories.film.GenreRepository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        GenreRepository.class,
        GenreRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreRepositoryTest {

    private final GenreRepository genreRepository;

    @Test
    void findAll_shouldReturnAllGenres() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres)
                .hasSize(6)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(
                        1L, 2L, 3L, 4L, 5L, 6L
                );
    }

    @Test
    void findById_shouldReturnGenre() {
        Optional<Genre> genreOptional =
                genreRepository.findById(1L);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    void findById_shouldReturnEmptyWhenGenreNotFound() {
        Optional<Genre> genreOptional =
                genreRepository.findById(999L);

        assertThat(genreOptional).isEmpty();
    }
}