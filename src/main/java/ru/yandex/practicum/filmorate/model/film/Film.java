package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @NotNull(message = "Описание не может быть null")
    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    String description;

    @NotNull(message = "Дата релиза должна быть указана")
    LocalDate releaseDate;

    @NotNull(message = "Продолжительность должна быть указана")
    int duration;

    Set<Genre> genre;

    MPA mpa;
}