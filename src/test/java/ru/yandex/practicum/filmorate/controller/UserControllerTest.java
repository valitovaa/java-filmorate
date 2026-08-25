package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserController controller = new UserController();

    @Test
    void addUser_whenValidUser_createsUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("Тест");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User result = controller.addUser(user);

        assertNotNull(result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("test", result.getLogin());
        assertEquals("Тест", result.getName());
        assertEquals(LocalDate.of(2000, 1, 1), result.getBirthday());
    }

    @Test
    void addUser_whenNameIsEmpty_usesLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User result = controller.addUser(user);

        assertEquals("test", result.getName());
    }

    @Test
    void findAll_returnsAllUsers() {
        User firstUser = new User();
        firstUser.setEmail("first@example.com");
        firstUser.setLogin("first");
        firstUser.setBirthday(LocalDate.of(2000, 1, 1));

        User secondUser = new User();
        secondUser.setEmail("second@example.com");
        secondUser.setLogin("second");
        secondUser.setBirthday(LocalDate.of(2001, 1, 1));

        controller.addUser(firstUser);
        controller.addUser(secondUser);

        assertEquals(2, controller.findAll().size());
    }

    @Test
    void update_whenUserExists_updatesUser() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setLogin("oldLogin");
        user.setName("Старое имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = controller.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("new@example.com");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("Новое имя");
        updatedUser.setBirthday(LocalDate.of(1999, 5, 10));

        User result = controller.update(updatedUser);

        assertEquals(createdUser.getId(), result.getId());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newLogin", result.getLogin());
        assertEquals("Новое имя", result.getName());
        assertEquals(LocalDate.of(1999, 5, 10), result.getBirthday());
    }

    @Test
    void update_whenIdIsMissing_throwsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> controller.update(user));

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void update_whenUserDoesNotExist_throwsException() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> controller.update(user));

        assertEquals("Пользователь с id = 999 не найден", exception.getMessage());
    }
}