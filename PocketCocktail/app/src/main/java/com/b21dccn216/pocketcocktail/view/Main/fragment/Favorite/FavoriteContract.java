package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.Favorite;

import java.util.List;

public interface FavoriteContract {
    interface View extends BaseContract.View{
        void showFavoriteList(List<Favorite> favoriteList);
    }

    interface Presenter extends BaseContract.Presenter<View>{

    }
}
