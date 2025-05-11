package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.UUID;

public class Ingredient implements Serializable {
    private String uuid;
    private String name;
    private String description;
    private String unit;
    private String image;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Ingredient() {
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public Ingredient(String name, String description, String unit) {
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public Ingredient(String name, String description, String unit, String image) {
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.image = image;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
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
        this.updatedAt = Timestamp.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = Timestamp.now();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        this.updatedAt = Timestamp.now();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        return "Ingredient{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", image='" + image + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
