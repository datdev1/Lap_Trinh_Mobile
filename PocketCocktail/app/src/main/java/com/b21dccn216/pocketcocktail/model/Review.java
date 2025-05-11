package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.UUID;

public class Review implements Serializable {
    private String uuid;
    private String drinkId;
    private String userId;
    private String comment;
    private double rate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Review() {
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public Review(String drinkId, String userId, String comment, double rate) {
        this.drinkId = drinkId;
        this.userId = userId;
        this.comment = comment;
        this.rate = rate;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
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
        this.updatedAt = Timestamp.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.updatedAt = Timestamp.now();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        this.updatedAt = Timestamp.now();
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
        this.updatedAt = Timestamp.now();
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "uuid='" + uuid + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", userId='" + userId + '\'' +
                ", comment='" + comment + '\'' +
                ", rate=" + rate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
