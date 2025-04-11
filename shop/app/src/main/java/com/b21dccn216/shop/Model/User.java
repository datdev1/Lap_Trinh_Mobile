package com.b21dccn216.shop.Model;


import java.io.Serializable;

public class User implements Serializable {
    private static final String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private String name;
    private String dob;
    private String gender;
    private String email;
    private String password;

    public User() { } // Required for Firebase






    // Getter va setter of properties

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


    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}


    // Functions check valid username and password (for login)
    public boolean isUsernameValid(){
        return email.matches(emailPattern);
    }

    public boolean isPasswordValid(){
        return password.matches(passwordPattern);
    }

    // Functions check error input (for show error message)
    public boolean isEmailError(){
        if(email.isEmpty()) return true;
        else{
            return isUsernameValid();
        }
    }

    public boolean isPasswordError() {
        if(password.isEmpty()) return true;
        return isPasswordValid();
    }

    public boolean isValidUser(){
        return isUsernameValid() && isPasswordValid();
    }

}