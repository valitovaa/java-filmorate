package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotNull(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан")
    private Boolean isPositive;

    @NotNull(message = "Id пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "Id фильма должен быть указан")
    private Long filmId;

}
