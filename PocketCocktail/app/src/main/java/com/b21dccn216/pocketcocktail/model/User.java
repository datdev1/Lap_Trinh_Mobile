package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final String RoleUser = "User";
    private static final String RoleAdmin = "Admin";
    private String uuid;
    private String saveUuidFromAuthen;
    private String name;
    private String email;
    private String role;
    private String password;
    private String image;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Dành riêng cho Authen
    public User(String saveUuidFromAuthen, String email, String password) {
        this.saveUuidFromAuthen = saveUuidFromAuthen;
        this.email = email;
        this.password = password;
        this.role = User.RoleUser;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public User(String name, String email, String password, String image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.role = User.RoleUser;
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

    public String getSaveUuidFromAuthen() {
        return saveUuidFromAuthen;
    }

    public void setSaveUuidFromAuthen(String saveUuidFromAuthen) {
        this.saveUuidFromAuthen = saveUuidFromAuthen;
        this.updatedAt = Timestamp.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = Timestamp.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = Timestamp.now();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = Timestamp.now();
    }

    public String getImage() {
        return image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        this.updatedAt = Timestamp.now();
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
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", saveUuidFromAuthen='" + saveUuidFromAuthen + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
