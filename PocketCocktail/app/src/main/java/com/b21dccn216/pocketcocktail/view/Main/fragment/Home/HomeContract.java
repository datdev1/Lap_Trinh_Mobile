package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.view.Login.LoginContract;
import com.b21dccn216.pocketcocktail.view.Login.model.User;
import com.b21dccn216.pocketcocktail.view.Main.Drink;

import java.util.List;

public interface HomeContract {
    interface View extends BaseContract.View {
        // Define view methods
        void showDrinks(List<Drink> drinks);

    }

    interface Presenter extends BaseContract.Presenter<View> {
        // Define presenter methods

    }
}
