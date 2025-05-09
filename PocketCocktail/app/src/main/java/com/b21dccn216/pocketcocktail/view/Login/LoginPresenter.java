package com.b21dccn216.pocketcocktail.view.Login;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.view.Login.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginPresenter extends BasePresenter<LoginContract.View>  implements LoginContract.Presenter{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public LoginPresenter() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize resource
    }

    @Override
    public void loginByEmailAndPassword(User user){
        view.showLoading(true);
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(((Fragment)view).getActivity(),task -> {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        view.loginSuccess();
                    }else{
                        view.showError(" login failed " + task.getException().getMessage());
                    }
                });
        }

}
