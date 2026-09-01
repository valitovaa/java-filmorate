package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final List<Friendship> friendships = new ArrayList<>();

    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (!StringUtils.hasText(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Friendship friendship = new Friendship(
                userId,
                friendId,
                FriendshipStatus.UNCONFIRMED
        );

        friendships.add(friendship);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        Friendship friendship = friendships.stream()
                .filter(f -> f.getUserId().equals(friendId))
                .filter(f -> f.getFriendId().equals(userId))
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Запрос на добавление в друзья не найден")
                );

        friendship.setStatus(FriendshipStatus.CONFIRMED);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        friendships.removeIf(f ->
                (f.getUserId().equals(userId) && f.getFriendId().equals(friendId))
                        ||
                        (f.getUserId().equals(friendId) && f.getFriendId().equals(userId))
        );
    }

    @Override
    public List<User> getFriends(Long userId) {
        return friendships.stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .filter(f ->
                        f.getUserId().equals(userId)
                                || f.getFriendId().equals(userId)
                )
                .map(f -> {
                    if (f.getUserId().equals(userId)) {
                        return users.get(f.getFriendId());
                    } else {
                        return users.get(f.getUserId());
                    }
                })
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {

        Set<Long> userFriendIds = getFriends(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return getFriends(otherUserId).stream()
                .filter(user -> userFriendIds.contains(user.getId()))
                .toList();
    }

    private long getNextId() {
        return ++currentId;
    }

}
