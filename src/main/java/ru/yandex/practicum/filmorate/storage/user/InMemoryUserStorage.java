package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0; // Безопасный счетчик вместо стрима

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        validateLogin(user);
        validateBirthday(user);

        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }

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

        validateLogin(newUser);
        validateBirthday(newUser);

        if (!StringUtils.hasText(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        // Обновляем пользователя в памяти целиком
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    // Быстрая генерация ID, которая не вешает терминал при тестах Newman
    private long getNextId() {
        return ++currentId;
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
