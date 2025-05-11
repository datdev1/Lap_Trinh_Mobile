package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Recipe implements Serializable {
    private String uuid;
    private String drinkId;
    private String ingredientId;
    private double amount;
    private Date createdAt;
    private Date updatedAt;

    public Recipe() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Recipe(String drinkId, String ingredientId, double quantity) {
        this.drinkId = drinkId;
        this.ingredientId = ingredientId;
        this.amount = quantity;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
        this.updatedAt = new Date();
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
        this.updatedAt = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }



    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getCreatedAtTimestamp() {
        return new Timestamp(createdAt);
    }

    public void setCreatedAtTimestamp(Timestamp timestamp) {
        this.createdAt = timestamp.toDate();
    }

    public Timestamp getUpdatedAtTimestamp() {
        return new Timestamp(updatedAt);
    }

    public void setUpdatedAtTimestamp(Timestamp timestamp) {
        this.updatedAt = timestamp.toDate();
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "uuid='" + uuid + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", ingredientId='" + ingredientId + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
