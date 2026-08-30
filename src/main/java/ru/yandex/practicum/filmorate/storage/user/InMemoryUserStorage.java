package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, Set<Long>> friends = new HashMap<>();

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

        friends.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);

        friends.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);

    }

    @Override
    public void removeFriend(Long userId, Long friendId) {

        Set<Long> userFriends = friends.get(userId);

        if (userFriends != null) {
            userFriends.remove(friendId);
        }

        Set<Long> friendFriends = friends.get(friendId);

        if (friendFriends != null) {
            friendFriends.remove(userId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet()).stream().map(users::get).toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        Set<Long> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Long> otherUserFriends = friends.getOrDefault(otherUserId, Collections.emptySet());

        return userFriends.stream().filter(otherUserFriends::contains).map(users::get).toList();
    }

    private long getNextId() {
        return ++currentId;
    }

}
