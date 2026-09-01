package ru.yandex.practicum.filmorate.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class User {

    Long id;

    @NotBlank(message = "Логин не может быть пустым")
    String login;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    String email;

    @NotNull(message = "Дата рождения должна быть указана")
    LocalDate birthday;

    String name;
}