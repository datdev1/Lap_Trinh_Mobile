package com.b21dccn216.pocketcocktail.model;

import java.util.UUID;

public class Review {
    private String uuid;
    private String drinkId;
    private String userId;
    private String comment;
    private double rate;

    public Review() {
    }

    public Review(String drinkId, String userId, String comment, double rate) {
        this.drinkId = drinkId;
        this.userId = userId;
        this.comment = comment;
        this.rate = rate;
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Review{" +
                "uuid='" + uuid + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", userId='" + userId + '\'' +
                ", comment='" + comment + '\'' +
                ", rate=" + rate +
                '}';
    }
}
