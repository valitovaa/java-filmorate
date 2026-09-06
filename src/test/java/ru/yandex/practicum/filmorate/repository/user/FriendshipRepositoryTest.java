package ru.yandex.practicum.filmorate.repository.user;

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
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FriendshipRepository.class,
        FriendshipRowMapper.class,
        UserRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipRepositoryTest {

    private final FriendshipRepository friendshipRepository;
    private final JdbcTemplate jdbc;

    @Test
    void addFriend_shouldAddFriendship() {

        insertUser(1L, "user1", "user1@email.com", "User One");
        insertUser(2L, "user2", "user2@email.com", "User Two");

        friendshipRepository.addFriend(1L, 2L);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friendships " +
                        "WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                1L,
                2L
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void removeFriend_shouldRemoveFriendship() {

        insertUser(1L, "user1", "user1@email.com", "User One");
        insertUser(2L, "user2", "user2@email.com", "User Two");

        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                1L,
                2L
        );

        friendshipRepository.removeFriend(1L, 2L);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friendships " +
                        "WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                1L,
                2L
        );

        assertThat(count).isEqualTo(0);
    }

    @Test
    void getFriends_shouldReturnUserFriends() {

        insertUser(1L, "user1", "user1@email.com", "User One");
        insertUser(2L, "user2", "user2@email.com", "User Two");
        insertUser(3L, "user3", "user3@email.com", "User Three");

        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                1L,
                2L
        );

        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                1L,
                3L
        );

        List<User> friends = friendshipRepository.getFriends(1L);

        assertThat(friends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriends() {

        insertUser(1L, "user1", "user1@email.com", "User One");
        insertUser(2L, "user2", "user2@email.com", "User Two");
        insertUser(3L, "user3", "user3@email.com", "User Three");
        insertUser(4L, "user4", "user4@email.com", "User Four");

        // У пользователя 1 друзья: 3 и 4
        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                1L,
                3L
        );

        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                1L,
                4L
        );

        // У пользователя 2 друзья: 3
        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                2L,
                3L
        );

        List<User> commonFriends =
                friendshipRepository.getCommonFriends(1L, 2L);

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(3L);
    }

    private void insertUser(
            Long id,
            String login,
            String email,
            String name
    ) {
        jdbc.update(
                "INSERT INTO users (id, login, email, birthday, name) " +
                        "VALUES (?, ?, ?, ?, ?)",
                id,
                login,
                email,
                LocalDate.of(1990, 1, 1),
                name
        );
    }
}

