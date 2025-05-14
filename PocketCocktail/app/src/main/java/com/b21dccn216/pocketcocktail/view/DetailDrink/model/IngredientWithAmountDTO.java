package com.b21dccn216.pocketcocktail.view.DetailDrink.model;

import com.b21dccn216.pocketcocktail.model.Ingredient;

import java.io.Serializable;

public class IngredientWithAmountDTO implements Serializable {
    private Ingredient ingredient;
    private double amount;
    private boolean isHave;

    public IngredientWithAmountDTO(Ingredient ingredient, double amount, boolean isHave) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.isHave = isHave;
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

    public boolean isHave() {
        return isHave;
    }

    public void setHave(boolean isHave) {
        this.isHave = isHave;
    }
}
