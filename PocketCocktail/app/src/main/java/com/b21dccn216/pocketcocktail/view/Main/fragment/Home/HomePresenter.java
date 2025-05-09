package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.util.Log;

import androidx.annotation.NonNull;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.view.Main.Drink;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter
    extends BasePresenter<HomeContract.View>
    implements HomeContract.Presenter
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference drinkRef = database.getReference("Drinks");


    public HomePresenter() {
        super();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDrinks();
    }

    public void getDrinks() {
        drinkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Drink> drinkList = new ArrayList<>();
                for (DataSnapshot drinkSnapshot : snapshot.getChildren()) {
                    Drink drink = drinkSnapshot.getValue(Drink.class);
                    drinkList.add(drink);
                }

                view.showDrinks(drinkList);

                // You can now use drinkList to populate RecyclerView or something else
                Log.d("DRINKS", "Loaded " + drinkList.size() + " drinks.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DRINKS", "Failed to read drinks", error.toException());
            }
        });
    }


}
