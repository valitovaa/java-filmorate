package ru.yandex.practicum.filmorate.repository.film;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mappers.film.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.repositories.film.DirectorRepository;
import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        DirectorRepository.class,
        DirectorRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorRepositoryTest {
    private final DirectorRepository directorRepository;
    private final JdbcTemplate jdbc;

    @Test
    void shouldCreateDirector() {
        Director director = new Director();
        director.setName("Иванов Иван Иванович");

        directorRepository.create(director);

        Optional<Director> directorOptional = directorRepository.getDirectorById(director.getId());
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director1 ->
                        assertThat(director1).hasFieldOrPropertyWithValue("name", "Иванов Иван Иванович")
                );
    }

    @Test
    void shouldGetDirectorById() {
        Director director = new Director();
        director.setName("Иванов Иван Иванович");
        directorRepository.create(director);

        Director director1 = new Director();
        director1.setName("Петров Пётр Петрович");
        directorRepository.create(director1);

        Optional<Director> directorOptional = directorRepository.getDirectorById(director.getId());
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(directorAct ->
                        assertThat(directorAct).hasFieldOrPropertyWithValue("name", "Иванов Иван Иванович")
                );

        Optional<Director> directorOptional1 = directorRepository.getDirectorById(director1.getId());
        assertThat(directorOptional1)
                .isPresent()
                .hasValueSatisfying(directorAct ->
                        assertThat(directorAct).hasFieldOrPropertyWithValue("name", "Петров Пётр Петрович")
                );
    }

    @Test
    void shouldUpdateDirector() {
        Director director = new Director();
        director.setName("Иванов Иван Иванович");

        directorRepository.create(director);

        Optional<Director> directorOptional = directorRepository.getDirectorById(director.getId());
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director1 ->
                        assertThat(director1).hasFieldOrPropertyWithValue("name", "Иванов Иван Иванович")
                );

        director.setName("Петров Пётр Петрович");
        directorRepository.update(director);

        Optional<Director> directorUpdated = directorRepository.getDirectorById(director.getId());
        assertThat(directorUpdated)
                .isPresent()
                .hasValueSatisfying(director1 ->
                        assertThat(director1).hasFieldOrPropertyWithValue("name", "Петров Пётр Петрович")
                );
    }
}
