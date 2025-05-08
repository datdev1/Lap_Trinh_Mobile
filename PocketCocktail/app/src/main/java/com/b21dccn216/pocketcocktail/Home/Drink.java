package com.b21dccn216.pocketcocktail.Home;

public class Drink {
    public int id;
    public int spiritId;
    public String name;
    public String img;
    public String description;
    public double rate;
    public String tip;

    public Drink() {
        // Needed for Firebase
    }

    public Drink(int id, int spiritId, String name, String img, String description, double rate, String tip) {
        this.id = id;
        this.spiritId = spiritId;
        this.name = name;
        this.img = img;
        this.description = description;
        this.rate = rate;
        this.tip = tip;
    }
}
