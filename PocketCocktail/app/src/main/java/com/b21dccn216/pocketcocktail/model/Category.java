package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Category implements Serializable {
    private String uuid;
    private String name;
    private String description;
    private String image;
    private Date createdAt;
    private Date updatedAt;



    public Category() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Category(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
        this.updatedAt = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Date();
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        return "Category{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
