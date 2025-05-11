package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.UUID;

public class Recipe implements Serializable {
    private String uuid;
    private String drinkId;
    private String ingredientId;
    private double amount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Recipe() {
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public Recipe(String drinkId, String ingredientId, double quantity) {
        this.drinkId = drinkId;
        this.ingredientId = ingredientId;
        this.amount = quantity;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getUuid() {
        return uuid;
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
        this.updatedAt = Timestamp.now();
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
        this.updatedAt = Timestamp.now();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
