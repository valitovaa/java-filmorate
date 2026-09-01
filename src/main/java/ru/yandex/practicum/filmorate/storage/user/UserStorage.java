package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    User addUser(User user);

    User update(User newUser);

    Optional<User> findUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void confirmFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherUserId);

}
