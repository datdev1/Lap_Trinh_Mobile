package com.b21dccn216.pocketcocktail.Home;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.Home.Adapter.CocktailHomeItemAdapter;
import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding  binding;
    private CocktailHomeItemAdapter adapter;
    private int column = 2;

    // Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference drinkRef = database.getReference("Drinks");

    private List<Drink> drinkList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new CocktailHomeItemAdapter(getApplicationContext(), drinkList);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(
//                getApplicationContext(),
//                column,
//                GridLayoutManager.VERTICAL,
//                false
//        );
//        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, int itemPosition, @NonNull RecyclerView parent) {
//                super.getItemOffsets(outRect, itemPosition, parent);
//                int spacing = getResources().getDimensionPixelSize(R.dimen.margin_8);
//
////                outRect.left = spacing;
////                outRect.right = spacing;
//
//                if (itemPosition < column) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            }
//        };
//        binding.recyclerView.setLayoutManager(gridLayoutManager);

        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        binding.recyclerView.setAdapter(adapter);


        drinkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                drinkList.clear();
                for (DataSnapshot drinkSnapshot : snapshot.getChildren()) {
                    Drink drink = drinkSnapshot.getValue(Drink.class);
                    drinkList.add(drink);
                }
                adapter.notifyDataSetChanged();

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