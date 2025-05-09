package com.b21dccn216.pocketcocktail.model;

import java.util.UUID;

public class Recipe {
    private String uuid;
    private String drinkId;
    private String ingredientId;
    private double quantity;

    public Recipe() {
    }

    public Recipe(String drinkId, String ingredientId, double quantity) {
        this.drinkId = drinkId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
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
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "uuid='" + uuid + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", ingredientId='" + ingredientId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
