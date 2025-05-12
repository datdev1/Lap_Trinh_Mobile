package com.b21dccn216.pocketcocktail.model;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class User implements Serializable {
    public static final String RoleUser = "User";
    public static final String RoleAdmin = "Admin";
    private String uuid;
    private String saveUuidFromAuthen;
    private String name;
    private String email;
    private String role;
    private String password;
    private String image;
    private Date createdAt;
    private Date updatedAt;

    public User() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Dành riêng cho Authen
    public User(String saveUuidFromAuthen, String email, String password) {
        this.saveUuidFromAuthen = saveUuidFromAuthen;
        this.email = email;
        this.password = password;
        this.role = User.RoleUser;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public User(String name, String email, String password, String image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.role = User.RoleUser;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String generateUUID() {
        String newUuid = UUID.randomUUID().toString();
        this.uuid = newUuid;
        return newUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSaveUuidFromAuthen() {
        return saveUuidFromAuthen;
    }

    public void setSaveUuidFromAuthen(String saveUuidFromAuthen) {
        this.saveUuidFromAuthen = saveUuidFromAuthen;
        this.updatedAt = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = new Date();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = new Date();
    }

    public String getImage() {
        return image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        this.updatedAt = new Date();
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
