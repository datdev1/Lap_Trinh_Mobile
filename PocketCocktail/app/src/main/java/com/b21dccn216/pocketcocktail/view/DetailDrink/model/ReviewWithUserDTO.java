package com.b21dccn216.pocketcocktail.view.DetailDrink.model;

import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;

public class ReviewWithUserDTO {
    private Review review;
    private User user;

    public ReviewWithUserDTO(Review review, User user) {
        this.review = review;
        this.user = user;
    }

    public Review getReview() {
        return review;
    }

    public User getUser() {
        return user;
    }
}
