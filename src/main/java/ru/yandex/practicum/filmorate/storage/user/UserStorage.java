package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User addUser(User user);

    User update(User newUser);

    Optional<User> findUserById(Long id);
}
