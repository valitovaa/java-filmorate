package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class User {

    @NotNull(message = "Id должен быть указан")
    Long id;

    @NotBlank(message = "Электронная почта должна быть указана")
    @Email(message = "Некорректный формат электронной почты")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    String login;

    String name;

    @NotNull(message = "Дата рождения должна быть указана")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}