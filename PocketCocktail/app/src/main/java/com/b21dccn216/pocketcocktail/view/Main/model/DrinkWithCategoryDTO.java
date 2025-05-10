package com.b21dccn216.pocketcocktail.view.Main.model;

import com.b21dccn216.pocketcocktail.model.Drink;

public class DrinkWithCategoryDTO{
    private String categoryName;
    private Drink drink;

    public DrinkWithCategoryDTO(String categoryName, Drink drink) {
        this.categoryName = categoryName;
        this.drink = drink;

    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }
}
