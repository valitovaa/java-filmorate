package ru.yandex.practicum.filmorate.dto.review;

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

    public boolean hasContent() {
        return (content != null && !content.isBlank());
    }

    public boolean hasPositive() {
        return isPositive != null;
    }

}
