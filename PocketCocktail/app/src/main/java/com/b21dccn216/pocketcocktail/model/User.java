package com.b21dccn216.pocketcocktail.model;

import java.util.UUID;

public class User {
    private static final String RoleUser = "User";
    private static final String RoleAdmin = "Admin";
    private String uuid;
    private String saveUuidFromAuthen;
    private String name;
    private String email;
    private String role;
    private String password;
    private String image;

    public User() {
    }

    // Dành riêng cho Authen
    public User(String saveUuidFromAuthen, String email, String password) {
        this.saveUuidFromAuthen = saveUuidFromAuthen;
        this.email = email;
        this.password = password;
        this.role = User.RoleUser;
    }

    public User(String name, String email, String password, String image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.role = User.RoleUser;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
