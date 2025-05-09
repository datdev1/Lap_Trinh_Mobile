package com.b21dccn216.pocketcocktail.model;

import java.util.UUID;

public class Favorite {
    private String uuid;
    private String drinkId;
    private String userId;

    public Favorite() {
    }

    public Favorite(String drinkId, String userId) {
        this.drinkId = drinkId;
        this.userId = userId;
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "uuid='" + uuid + '\'' +
                ", drinkId='" + drinkId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
