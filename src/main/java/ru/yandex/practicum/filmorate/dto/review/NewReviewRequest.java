package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан")
    private Boolean isPositive;

    @NotNull(message = "Id пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "Id фильма должен быть указан")
    private Long filmId;

}
