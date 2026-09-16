package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull(message = "Id отзыва должен быть указан")
    @Min(value = 1, message = "Id отзыва должно быть числом положительным")
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    @Min(value = 1, message = "Id пользователя быть числом положительным")
    private Long userId;
    @Min(value = 1, message = "Id фильма должно быть числом положительным")
    private Long filmId;
    private Long useful;

    public boolean hasContent() {
        return !(content == null || content.isBlank());
    }

    public boolean hasPositive() {
        return isPositive != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasFilmId() {
        return filmId != null;
    }

    public boolean hasUseful() {
        return useful != null;
    }
}
