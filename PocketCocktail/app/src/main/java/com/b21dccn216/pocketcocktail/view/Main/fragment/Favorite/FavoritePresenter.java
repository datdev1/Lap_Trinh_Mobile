package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.List;

public class FavoritePresenter extends BasePresenter<FavoriteContract.View>
        implements FavoriteContract.Presenter {
    private FavoriteDAO favoriteDAO = new FavoriteDAO();

    public FavoritePresenter(){
        super();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getAllFavoriteItem();

    }

    private void getAllFavoriteItem() {
        favoriteDAO.getFavoritesByUserId(FirebaseAuth.getInstance().getCurrentUser().getUid(),
        ); {
            @Override
            public void onFavoriteListLoaded(List<Favorite> favorites) {
                view.showFavoriteList(favorites);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
