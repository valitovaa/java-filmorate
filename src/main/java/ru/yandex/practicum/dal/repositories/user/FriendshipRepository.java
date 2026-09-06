package ru.yandex.practicum.dal.repositories.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {

    private static final String INSERT_QUERY =
            "INSERT INTO friendships(user_id, friend_id) " +
                    "VALUES (?, ?)";

    private static final String DELETE_QUERY =
            "DELETE FROM friendships " +
                    "WHERE user_id = ? AND friend_id = ?";

    private static final String FIND_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friendships f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ?";

    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friendships f1 ON u.id = f1.friend_id " +
                    "JOIN friendships f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = ? " +
                    "AND f2.user_id = ?";

    private final RowMapper<User> userMapper;

    public FriendshipRepository(
            JdbcTemplate jdbc,
            RowMapper<Friendship> mapper,
            RowMapper<User> userMapper
    ) {
        super(jdbc, mapper);
        this.userMapper = userMapper;
    }

    public void addFriend(Long userId, Long friendId) {
        jdbc.update(
                INSERT_QUERY,
                userId,
                friendId
        );
    }

    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(
                DELETE_QUERY,
                userId,
                friendId
        );
    }

    public List<User> getFriends(Long userId) {
        return jdbc.query(
                FIND_FRIENDS_QUERY,
                userMapper,
                userId
        );
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return jdbc.query(
                FIND_COMMON_FRIENDS_QUERY,
                userMapper,
                userId,
                otherUserId
        );
    }
}