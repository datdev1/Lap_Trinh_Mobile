package com.b21dccn216.pocketcocktail.view.DetailDrink.model;

import com.b21dccn216.pocketcocktail.model.Ingredient;

public class IngredientWithAmountDTO {
    private Ingredient ingredient;
    private double amount;

    public IngredientWithAmountDTO(Ingredient ingredient, double amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
