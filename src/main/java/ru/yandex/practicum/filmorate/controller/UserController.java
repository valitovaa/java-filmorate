package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {

        validateLogin(user);
        validateBirthday(user);

        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {

        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        validateLogin(newUser);
        validateBirthday(newUser);

        User oldUser = users.get(newUser.getId());

        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

        return oldUser;
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
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