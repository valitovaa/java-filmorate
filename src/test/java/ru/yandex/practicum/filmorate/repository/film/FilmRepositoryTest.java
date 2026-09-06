package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.dal.mappers.film.FilmRowMapper;
import ru.yandex.practicum.dal.repositories.film.FilmRepository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmRepository.class,
        FilmRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmRepositoryTest {

    private final FilmRepository filmRepository;
    private final JdbcTemplate jdbc;

    @Test
    void findAll_shouldReturnAllFilms() {

        insertFilm(
                1L,
                "Film One",
                "Description One",
                LocalDate.of(2000, 1, 1),
                120,
                "G"
        );

        insertFilm(
                2L,
                "Film Two",
                "Description Two",
                LocalDate.of(2005, 5, 5),
                90,
                "PG"
        );

        List<Film> films = filmRepository.findAll();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void findById_shouldReturnFilm() {

        insertFilm(
                1L,
                "Test Film",
                "Test Description",
                LocalDate.of(2000, 1, 1),
                120,
                "G"
        );

        Optional<Film> filmOptional =
                filmRepository.findById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue(
                                        "name", "Test Film"
                                )
                                .hasFieldOrPropertyWithValue(
                                        "description", "Test Description"
                                )
                                .hasFieldOrPropertyWithValue(
                                        "duration", 120
                                )
                                .hasFieldOrPropertyWithValue(
                                        "mpa", MPA.G
                                )
                );
    }

    @Test
    void findById_shouldReturnEmptyWhenFilmNotFound() {

        Optional<Film> filmOptional =
                filmRepository.findById(999L);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    void addFilm_shouldSaveFilmAndGenerateId() {

        Film film = new Film();
        film.setName("New Film");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);
        film.setMpa(MPA.PG);

        Film savedFilm = filmRepository.addFilm(film);

        assertThat(savedFilm.getId()).isGreaterThan(0L);
        assertThat(savedFilm.getName()).isEqualTo("New Film");
        assertThat(savedFilm.getDescription()).isEqualTo("New Description");
        assertThat(savedFilm.getMpa()).isEqualTo(MPA.PG);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM films WHERE id = ?",
                Integer.class,
                savedFilm.getId()
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void update_shouldUpdateFilm() {

        insertFilm(
                1L,
                "Old Film",
                "Old Description",
                LocalDate.of(2000, 1, 1),
                90,
                "G"
        );

        Film film = new Film();
        film.setId(1L);
        film.setName("New Film");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 2));
        film.setDuration(150);
        film.setMpa(MPA.PG_13);

        Film updatedFilm = filmRepository.update(film);

        assertThat(updatedFilm)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "New Film")
                .hasFieldOrPropertyWithValue(
                        "description", "New Description"
                )
                .hasFieldOrPropertyWithValue(
                        "duration", 150
                )
                .hasFieldOrPropertyWithValue(
                        "mpa", MPA.PG_13
                );

        Film dbFilm = jdbc.queryForObject(
                "SELECT * FROM films WHERE id = ?",
                new FilmRowMapper(),
                1L
        );

        assertThat(dbFilm)
                .hasFieldOrPropertyWithValue("name", "New Film")
                .hasFieldOrPropertyWithValue(
                        "description", "New Description"
                )
                .hasFieldOrPropertyWithValue(
                        "duration", 150
                )
                .hasFieldOrPropertyWithValue(
                        "mpa", MPA.PG_13
                );
    }

    @Test
    void findPopularFilms_shouldReturnFilmsOrderedByLikes() {

        insertFilm(
                1L,
                "Film One",
                "Description One",
                LocalDate.of(2000, 1, 1),
                120,
                "G"
        );

        insertFilm(
                2L,
                "Film Two",
                "Description Two",
                LocalDate.of(2005, 5, 5),
                90,
                "PG"
        );

        insertFilm(
                3L,
                "Film Three",
                "Description Three",
                LocalDate.of(2010, 10, 10),
                100,
                "R"
        );

        // Создаём пользователей, которые будут ставить лайки
        insertUser(1L);
        insertUser(2L);
        insertUser(3L);

        // Film 1 — 3 лайка
        insertLike(1L, 1L);
        insertLike(1L, 2L);
        insertLike(1L, 3L);

        // Film 2 — 1 лайк
        insertLike(2L, 1L);

        // Film 3 — 2 лайка
        insertLike(3L, 1L);
        insertLike(3L, 2L);

        List<Film> popularFilms =
                filmRepository.findPopularFilms(3);

        assertThat(popularFilms)
                .extracting(Film::getId)
                .containsExactly(1L, 3L, 2L);
    }

    private void insertFilm(
            Long id,
            String name,
            String description,
            LocalDate releaseDate,
            Integer duration,
            String mpa
    ) {
        jdbc.update(
                "INSERT INTO films " +
                        "(id, name, description, release_date, duration, mpa) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                id,
                name,
                description,
                releaseDate,
                duration,
                mpa
        );
    }

    private void insertUser(Long id) {
        jdbc.update(
                "INSERT INTO users " +
                        "(id, login, email, birthday, name) " +
                        "VALUES (?, ?, ?, ?, ?)",
                id,
                "user" + id,
                "user" + id + "@email.com",
                LocalDate.of(1990, 1, 1),
                "User " + id
        );
    }

    private void insertLike(Long filmId, Long userId) {
        jdbc.update(
                "INSERT INTO film_likes (film_id, user_id) " +
                        "VALUES (?, ?)",
                filmId,
                userId
        );
    }
}

