package com.b21dccn216.pocketcocktail.model;

import java.io.Serializable;
import java.util.UUID;

public class Drink implements Serializable {
    private String uuid;
    private String name;
    private String userId;
    private String image;
    private String categoryId;
    private String instruction;
    private String description;
    private double rate;


    public Drink() {
    }

    public Drink(String name, String categoryId, String instruction, String description, double rate) {
        this.name = name;
        this.categoryId = categoryId;
        this.instruction = instruction;
        this.description = description;
        this.rate = rate;
    }

    public Drink(String name, String userId, String image, String categoryId, String instruction, String description, double rate) {
        this.name = name;
        this.userId = userId;
        this.image = image;
        this.categoryId = categoryId;
        this.instruction = instruction;
        this.description = description;
        this.rate = rate;
    }

    public String getUuid() {
        return uuid;
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Drink{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", image='" + image + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", instruction='" + instruction + '\'' +
                ", description='" + description + '\'' +
                ", rate=" + rate +
                '}';
    }
}
