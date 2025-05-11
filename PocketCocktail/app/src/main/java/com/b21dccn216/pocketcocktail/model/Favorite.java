package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Favorite implements Serializable {
    private String uuid;
    private String drinkId;
    private String userId;
    private Date createdAt;


    private Date updatedAt;

    public Favorite() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Favorite(String drinkId, String userId) {
        this.drinkId = drinkId;
        this.userId = userId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getUuid() {
        return uuid;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
        this.updatedAt = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return "Favorite{" +
                "uuid='" + uuid + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
