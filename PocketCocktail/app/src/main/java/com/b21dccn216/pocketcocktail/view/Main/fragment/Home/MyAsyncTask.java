package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.os.AsyncTask;
import android.os.Build;

import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class MyAsyncTask extends AsyncTask<Drink, DrinkWithCategoryDTO, List<DrinkWithCategoryDTO>> {

    CategoryDAO categoryDAO = new CategoryDAO();
    private AsyncTaskCallBack callBack;

    public MyAsyncTask(AsyncTaskCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    protected List<DrinkWithCategoryDTO> doInBackground(Drink... drinks) {
        List<DrinkWithCategoryDTO> drinkList = new ArrayList<>();
        for (Drink drink : drinks) {
            categoryDAO.getCategory(drink.getCategoryId(),
                    documentSnapshot -> {
                        Category category = documentSnapshot.toObject(Category.class);
                        if (category == null) {
                            drinkList.add(new DrinkWithCategoryDTO(drink.getCategoryId(), drink));
                        } else {
                            drinkList.add(new DrinkWithCategoryDTO(category.getName(), drink));
                        }
//                        publishProgress(drinkList);
                    },
                    e -> {

                    });
        }
        return drinkList;
    }

    @Override
    protected void onPostExecute(List<DrinkWithCategoryDTO> category) {
        super.onPostExecute(category);
        callBack.onPostExecute(category);
    }



    public interface AsyncTaskCallBack {
        void onPostExecute(List<DrinkWithCategoryDTO> category);
        void onPostExecute();
    }

}
