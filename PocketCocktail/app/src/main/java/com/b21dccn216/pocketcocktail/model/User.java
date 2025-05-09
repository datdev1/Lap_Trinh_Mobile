package com.b21dccn216.pocketcocktail.model;

import java.util.UUID;

public class User {
    private String uuid;
    private String saveUuidFromAuthen;
    private String name;
    private String email;
    private String password;
    private String image;

    public User() {
    }

    public User(String name, String email, String password, String image) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getSaveUuidFromAuthen() {
        return saveUuidFromAuthen;
    }

    public void setSaveUuidFromAuthen(String saveUuidFromAuthen) {
        this.saveUuidFromAuthen = saveUuidFromAuthen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", saveUuidFromAuthen='" + saveUuidFromAuthen + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
