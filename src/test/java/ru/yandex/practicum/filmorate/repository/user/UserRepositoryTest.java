package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.yandex.practicum.filmorate.dal.mappers.user.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.repositories.user.UserRepository;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        UserRepository.class,
        UserRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbc;

    @Test
    void findAll_shouldReturnAllUsers() {

        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) VALUES (?, ?, ?, ?, ?)",
                1L,
                "login1",
                "user1@email.com",
                LocalDate.of(1990, 1, 1),
                "User One"
        );

        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) VALUES (?, ?, ?, ?, ?)",
                2L,
                "login2",
                "user2@email.com",
                LocalDate.of(1995, 5, 5),
                "User Two"
        );

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void findByEmail_shouldReturnUser() {

        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) VALUES (?, ?, ?, ?, ?)",
                1L,
                "testlogin",
                "test@email.com",
                LocalDate.of(1990, 1, 1),
                "Test User"
        );

        Optional<User> userOptional =
                userRepository.findByEmail("test@email.com");

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

    @Test
    void findByEmail_shouldReturnEmptyWhenUserNotFound() {

        Optional<User> userOptional =
                userRepository.findByEmail("unknown@email.com");

        assertThat(userOptional).isEmpty();
    }

    @Test
    void findById_shouldReturnUser() {

        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) VALUES (?, ?, ?, ?, ?)",
                1L,
                "testlogin",
                "test@email.com",
                LocalDate.of(1990, 1, 1),
                "Test User"
        );

        Optional<User> userOptional =
                userRepository.findById(1L);

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

    @Test
    void findById_shouldReturnEmptyWhenUserNotFound() {

        Optional<User> userOptional =
                userRepository.findById(999L);

        assertThat(userOptional).isEmpty();
    }

    @Test
    void addUser_shouldSaveUserAndGenerateId() {

        User user = new User();
        user.setLogin("newlogin");
        user.setEmail("new@email.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName("New User");

        User savedUser = userRepository.addUser(user);

        assertThat(savedUser.getId()).isGreaterThan(0L);
        assertThat(savedUser.getLogin()).isEqualTo("newlogin");
        assertThat(savedUser.getEmail()).isEqualTo("new@email.com");

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                savedUser.getId()
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void update_shouldUpdateUser() {

        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) VALUES (?, ?, ?, ?, ?)",
                1L,
                "oldlogin",
                "old@email.com",
                LocalDate.of(1990, 1, 1),
                "Old Name"
        );

        User user = new User();
        user.setId(1L);
        user.setLogin("newlogin");
        user.setEmail("new@email.com");
        user.setBirthday(LocalDate.of(2000, 2, 2));
        user.setName("New Name");

        User updatedUser = userRepository.update(user);

        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("login", "newlogin")
                .hasFieldOrPropertyWithValue("email", "new@email.com")
                .hasFieldOrPropertyWithValue("name", "New Name");

        User dbUser = jdbc.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                new UserRowMapper(),
                1L
        );

        assertThat(dbUser)
                .hasFieldOrPropertyWithValue("login", "newlogin")
                .hasFieldOrPropertyWithValue("email", "new@email.com")
                .hasFieldOrPropertyWithValue("name", "New Name")
                .hasFieldOrPropertyWithValue(
                        "birthday",
                        LocalDate.of(2000, 2, 2)
                );
    }
}

