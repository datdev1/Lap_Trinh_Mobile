package com.b21dccn216.pocketcocktail.view.Main.model;

import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;

import java.util.List;

public class CategoryWithDrinkDTO {
    private Category category;
    private List<Drink> drinks;

    public CategoryWithDrinkDTO(Category category, List<Drink> drinks) {
        this.category = category;
        this.drinks = drinks;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }
}
