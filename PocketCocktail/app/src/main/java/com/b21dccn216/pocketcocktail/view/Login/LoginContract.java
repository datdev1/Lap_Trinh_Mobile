package com.b21dccn216.pocketcocktail.view.Login;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.view.Login.model.User;

public interface LoginContract {
    interface View extends BaseContract.View {
        // Define view methods
         void loginSuccess();
         void showLoading(boolean isLoading);
    }

    interface Presenter extends BaseContract.Presenter<View> {
        // Define presenter methods
         void loginByEmailAndPassword(User user);
    }
}
