package com.b21dccn216.shop.Model;

public class UserFirebase {
    private String id;
    private String name;
    private String dob;
    private String gender;
    private String email;


    public UserFirebase() {
    }

    public UserFirebase(String id, String name, String dob, String gender, String email) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
