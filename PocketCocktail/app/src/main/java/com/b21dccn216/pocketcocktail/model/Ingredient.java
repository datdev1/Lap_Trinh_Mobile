package com.b21dccn216.pocketcocktail.model;

import java.util.UUID;

public class Ingredient {
    private String uuid;
    private String name;
    private String description;
    private String unit;

    private String image;

    public Ingredient() {
    }

    public Ingredient(String name, String description, String unit) {
        this.name = name;
        this.description = description;
        this.unit = unit;
    }

    public Ingredient(String name, String description, String unit, String image) {
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.image = image;
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }

    public String getUuid() {
        return uuid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
