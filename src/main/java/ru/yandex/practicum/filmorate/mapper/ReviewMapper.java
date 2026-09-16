package ru.yandex.practicum.filmorate.mapper;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.film.Review;

@Service
public class ReviewMapper {

    @Nonnull
    public static Review mapToReview(NewReviewRequest newReviewRequest) {
        Review review = new Review();
        review.setContent(newReviewRequest.getContent());
        review.setIsPositive(newReviewRequest.getIsPositive());
        review.setUserId(newReviewRequest.getUserId());
        review.setFilmId(newReviewRequest.getFilmId());

        return review;
    }

    public static void updateReviewFields(Review review, UpdateReviewRequest updateReview) {

        if (updateReview.hasContent()) {
            review.setContent(updateReview.getContent());
        }
        if (updateReview.hasPositive()) {
            review.setIsPositive(updateReview.getIsPositive());
        }
        if (updateReview.hasUserId()) {
            review.setUserId(updateReview.getUserId());
        }
        if (updateReview.hasFilmId()) {
            review.setFilmId(updateReview.getFilmId());
        }
        if (updateReview.hasUseful()) {
            review.setUseful(updateReview.getUseful());
        }

    }
}
