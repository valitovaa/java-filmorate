package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User user) {
        validateBirthday(user);
        validateLogin(user);
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User update(User user) {
        validateLogin(user);
        validateBirthday(user);
        return userStorage.update(user);
    }

    public Optional<User> findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        findUserOrThrow(userId);
        findUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        findUserOrThrow(userId);

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        findUserOrThrow(userId);
        findUserOrThrow(otherUserId);

        return userStorage.getCommonFriends(userId, otherUserId);
    }


    private User findUserOrThrow(Long id) {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    private void validateLogin(User user) {
        if (!StringUtils.hasText(user.getLogin())) {
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }
    }

    private void validateBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }
}