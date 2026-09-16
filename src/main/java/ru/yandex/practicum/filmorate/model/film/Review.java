package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;

@Data
public class Review {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Long useful = 0L;
}
