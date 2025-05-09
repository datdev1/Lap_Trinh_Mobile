package com.b21dccn216.pocketcocktail.view.Login;

import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.view.Login.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                        view.authSuccess();
                    }else{
                        view.authFail("Login failed: " + task.getException().getMessage());
                    }
                });
        }


    @Override
    public void signUpWithEmailAndPassword(User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // signup success
                        // TODO:: Xử lý đối tượng user, lưu thông tin phone, fullname và firebase
                        view.authSuccess();
                    }else{
                        view.authFail("Sign-up failed: " + task.getException().getMessage());
                    }
                });
    }
}
