package com.b21dccn216.shop.login;

import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.b21dccn216.shop.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginViewModel extends ViewModel
{

    private MutableLiveData<User> loginWithGoogleResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingAnimation = new MutableLiveData<>(false);

    public MutableLiveData<String> email = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public LiveData<Boolean> emailWarning = Transformations.map(email, this::isValidEmail);
    public LiveData<Boolean> passwordWarning = Transformations.map(password, this::isValidPassword);

    public MutableLiveData<Boolean> showPassword = new MutableLiveData<>(false);

    public LoginViewModel() {

    }

    public void onCasualLoginCLick(){

    }

    public void onGoogleLoginClick(){
    }

    private boolean isValidEmail(String email) {
        if(email.isEmpty()) return false;
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if(password.isEmpty()) return false;
        return password.length() < 6;
    }
    public void toggleShowPassword(){
        showPassword.setValue(Boolean.FALSE.equals(showPassword.getValue()));
    }

    public MutableLiveData<User> getLoginWithGoogleResponse() {
        return loginWithGoogleResponse;
    }

}
