package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.dal.mappers.user.FriendshipRowMapper;
import ru.yandex.practicum.dal.mappers.user.UserRowMapper;
import ru.yandex.practicum.dal.repositories.user.FriendshipRepository;
import ru.yandex.practicum.dal.repositories.user.UserRepository;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        UserDbStorage.class,
        UserRepository.class,
        FriendshipRepository.class,
        UserRowMapper.class,
        FriendshipRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbc;

    @Test
    public void testFindUserById() {

        // Добавляем тестового пользователя в таблицу,
        // которую создал schema.sql
        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) VALUES (?, ?, ?, ?, ?)",
                1L,
                "testlogin",
                "test@email.com",
                LocalDate.of(1990, 1, 1),
                "Test User"
        );

        // Вызываем тестируемый метод
        Optional<User> userOptional = userStorage.findUserById(1L);

        // Проверяем результат
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("login", "testlogin")
                                .hasFieldOrPropertyWithValue("email", "test@email.com")
                                .hasFieldOrPropertyWithValue("name", "Test User")
                );
    }
}