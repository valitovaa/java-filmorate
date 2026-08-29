package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    private final Map<Long, Set<Long>> friends = new HashMap<>();


    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Optional<User> findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        friends.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);

        friends.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        Set<Long> userFriends = friends.get(userId);

        if (userFriends != null) {
            userFriends.remove(friendId);
        }

        Set<Long> friendFriends = friends.get(friendId);

        if (friendFriends != null) {
            friendFriends.remove(userId);
        }
    }

    public List<User> getFriends(Long userId) {
        findUserOrThrow(userId);

        Set<Long> friendIds = friends.getOrDefault(userId, Collections.emptySet());

        return friendIds.stream()
                .map(userStorage::findUserById)
                .flatMap(Optional::stream)
                .toList();
    }



    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        findUserOrThrow(userId);
        findUserOrThrow(otherUserId);

        Set<Long> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Long> otherUserFriends = friends.getOrDefault(otherUserId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::findUserById)
                .flatMap(Optional::stream)
                .toList();
    }


    private User findUserOrThrow(Long id) {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }
}