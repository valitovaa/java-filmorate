package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.filmorate.dal.repositories.film.LikeRepository;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(LikeRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeRepositoryTest {

    private final LikeRepository likeRepository;
    private final JdbcTemplate jdbc;

    @Test
    void addLike_shouldAddLike() {
        insertFilm(1L);
        insertUser(1L);

        likeRepository.addLike(1L, 1L);

        assertThat(likeRepository.countLikes(1L))
                .isEqualTo(1);
    }

    @Test
    void deleteLike_shouldDeleteLike() {
        insertFilm(1L);
        insertUser(1L);

        likeRepository.addLike(1L, 1L);

        likeRepository.deleteLike(1L, 1L);

        assertThat(likeRepository.countLikes(1L))
                .isEqualTo(0);
    }

    @Test
    void countLikes_shouldReturnNumberOfLikes() {
        insertFilm(1L);

        insertUser(1L);
        insertUser(2L);
        insertUser(3L);

        likeRepository.addLike(1L, 1L);
        likeRepository.addLike(1L, 2L);
        likeRepository.addLike(1L, 3L);

        assertThat(likeRepository.countLikes(1L))
                .isEqualTo(3);
    }

    @Test
    void countLikes_shouldReturnZeroWhenFilmHasNoLikes() {
        insertFilm(1L);

        assertThat(likeRepository.countLikes(1L))
                .isZero();
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

    private void insertUser(Long id) {
        jdbc.update(
                """
                INSERT INTO users
                    (id, login, email, birthday, name)
                VALUES
                    (?, ?, ?, ?, ?)
                """,
                id,
                "user" + id,
                "user" + id + "@test.ru",
                "1990-01-01",
                "User " + id
        );
    }
}