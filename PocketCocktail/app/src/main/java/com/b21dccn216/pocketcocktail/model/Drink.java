package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;
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
    private Date createdAt;
    private Date updatedAt;

    public Drink() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Drink(String name, String categoryId, String instruction, String description, double rate) {
        this.name = name;
        this.categoryId = categoryId;
        this.instruction = instruction;
        this.description = description;
        this.rate = rate;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }



    public Drink(String name, String userId, String image, String categoryId, String instruction, String description, double rate) {
        this.name = name;
        this.userId = userId;
        this.image = image;
        this.categoryId = categoryId;
        this.instruction = instruction;
        this.description = description;
        this.rate = rate;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
        this.updatedAt = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.updatedAt = new Date();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        this.updatedAt = new Date();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = new Date();
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
        this.updatedAt = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Date();
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getCreatedAtTimestamp() {
        return new Timestamp(createdAt);
    }

    public void setCreatedAtTimestamp(Timestamp timestamp) {
        this.createdAt = timestamp.toDate();
    }

    public Timestamp getUpdatedAtTimestamp() {
        return new Timestamp(updatedAt);
    }

    public void setUpdatedAtTimestamp(Timestamp timestamp) {
        this.updatedAt = timestamp.toDate();
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
