package com.b21dccn216.pocketcocktail.view.Main.model;

import com.b21dccn216.pocketcocktail.model.Drink;

public class DrinkWithFavCount {
    private Drink drink;
    private int count;

    public DrinkWithFavCount(Drink drink, int count) {
        this.drink = drink;
        this.count = count;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
