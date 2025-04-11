package com.b21dccn216.shop.login;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.b21dccn216.shop.BR;
import com.b21dccn216.shop.Model.User;

public class LoginUiState extends BaseObservable {
    private User user = new User();
    private boolean isShowPassword =false;
    private MutableLiveData<Boolean> isLoginSuccess = new MutableLiveData<>(false);
//    private boolean isRemember = false;
    private boolean isLoginError = false;

    public LoginUiState(){
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // is show password
    @Bindable
    public boolean isShowPassword() {
        return isShowPassword;
    }
    public void toggleShowPassword(){
        isShowPassword = !isShowPassword;
        notifyPropertyChanged(BR.showPassword);
    }
    public void setShowPassword(boolean showPassword) {
        isShowPassword = showPassword;
        notifyPropertyChanged(BR.showPassword);
    }
    // end is show password

    public MutableLiveData<Boolean> getIsLoginSuccess() {
        return isLoginSuccess;
    }
    public void setIsLoginSuccess(MutableLiveData<Boolean> isLoginSuccess) {
        this.isLoginSuccess = isLoginSuccess;
    }

    @Bindable
    public boolean isLoginError() {
        return isLoginError;
    }
    public void setLoginError(boolean loginError) {
        isLoginError = loginError;
        notifyPropertyChanged(BR.loginError);
    }

    // Edit Text
    @Bindable
    public String getEmail(){return user.getEmail();}
    public void setEmail(String e){
        user.setEmail(e);
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword(){return user.getPassword();}
    public void setPassword(String p){
        user.setPassword(p);
        notifyPropertyChanged(BR.password);
    }

    // EditText Is Show Error
    @Bindable
    public boolean isShowEmailWarning(){
        return user.isEmailError();
    }
    @Bindable
    public boolean isShowPasswordWarning(){
        return user.isPasswordError();
    }

}
