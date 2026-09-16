package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addNewReview(@Valid @RequestBody NewReviewRequest review) {
        return reviewService.postReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody UpdateReviewRequest review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/")
    public void noneIdInDeleteRequest() {
        log.trace("Передан Delete запрос на удаление отзыва по id без id");
        throw new ConditionsNotMetException("Id должен быть указан.");
    }

    @DeleteMapping("/{id}")
    public void deleteReview(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {

        reviewService.deleteReview(id);
    }

    @GetMapping("/")
    public void noneIdInGetRequest() {
        log.trace("Передан Get запрос на получение отзыва по id без id");
        throw new ConditionsNotMetException("Id должен быть указан.");
    }

    @GetMapping("/{id}")
    public Review getReviewById(
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id) {

        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getAllReviewsByFilmId(
            @Min(value = 0, message = "Id должно быть числом положительным")
            @RequestParam(defaultValue = "0")
            Long filmId,

            @Min(value = 0, message = "Количество отзывов должно быть числом положительным")
            @RequestParam(defaultValue = "10")
            int count
    ) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping({"//like/{userId}", "/{id}/like/", "//like/", "//dislike/{userId}", "/{id}/dislike/", "//like/"})
    public void noneIdOfReviewIdOrUserIdOnPutRequest() {
        log.trace("Передан Put запрос на лайк/дизлайк отзыва пользователем без id");
        throw new ConditionsNotMetException("Id отзыва и/или id пользователя должны быть указаны.");
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLikeReview(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @NotNull(message = "UserId должен быть указан")
            @Min(value = 1, message = "UserId должно быть числом положительным")
            @PathVariable Long userId) {

        return reviewService.changeUsefulReview(id, userId, 1);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislikeReview(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @NotNull(message = "UserId должен быть указан")
            @Min(value = 1, message = "UserId должно быть числом положительным")
            @PathVariable Long userId) {

        return reviewService.changeUsefulReview(id, userId, 0);
    }

    @DeleteMapping({"//like/{userId}", "/{id}/like/", "//like/", "//dislike/{userId}", "/{id}/dislike/", "//like/"})
    public void noneIdOfReviewIdOrUserIdOnDeleteRequest() {
        log.trace("Передан Delete запрос на удаление лайка/дизлайка отзыва пользователем без id");
        throw new ConditionsNotMetException("Id отзыва и/или id пользователя должны быть указаны.");
    }

    @DeleteMapping({"/{id}/like/{userId}", "/{id}/dislike/{userId}"})
    public Review deleteLikeReview(
            @NotNull(message = "Id должен быть указан")
            @Min(value = 1, message = "Id должно быть числом положительным")
            @PathVariable Long id,

            @NotNull(message = "UserId должен быть указан")
            @Min(value = 1, message = "UserId должно быть числом положительным")
            @PathVariable Long userId) {

        return reviewService.changeUsefulReview(id, userId, 2);
    }
}
