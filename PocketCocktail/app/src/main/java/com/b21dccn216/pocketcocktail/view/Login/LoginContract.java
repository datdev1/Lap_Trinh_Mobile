package com.b21dccn216.pocketcocktail.view.Login;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.User;

public interface LoginContract {
    interface View extends BaseContract.View {
        // Define view methods
         void authSuccess();
         void showLoading(boolean isLoading);
         void authFail(String mess);
    }

    interface Presenter extends BaseContract.Presenter<View> {
        // Define presenter methods
         void loginByEmailAndPassword(User user);
         void signUpWithEmailAndPassword(User user, String confirmPassword);
    }
}
