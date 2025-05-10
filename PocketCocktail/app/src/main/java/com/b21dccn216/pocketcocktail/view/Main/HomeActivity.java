package com.b21dccn216.pocketcocktail.view.Main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.TestDatabaseActivity;
import com.b21dccn216.pocketcocktail.view.Login.LoginActivity;
import com.b21dccn216.pocketcocktail.view.Login.LoginContract;
import com.b21dccn216.pocketcocktail.view.Main.adapter.CocktailHomeItemAdapter;
import com.b21dccn216.pocketcocktail.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity{
    private ActivityHomeBinding  binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Check is admin to show/hide admin tab
        User user = SessionManager.getInstance().getUser();
        if(user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        boolean isAdmin = user.getRole().equals("Admin");
        binding.bottomNavigationView.getMenu().findItem(R.id.nav_admin).setVisible(isAdmin);

        //toolbar
        setSupportActionBar(binding.toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_discover, R.id.nav_favorite, R.id.nav_profile
        ).build();
        //bottom navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        NavController navCo = navHostFragment.getNavController();

        // Bind toolbar & bottomNav with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navCo);
        NavigationUI.setupWithNavController(binding.toolbar, navCo, appBarConfiguration);

        // Handle admin tab
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            if(item.getItemId() == R.id.nav_admin){
                Intent intent = new Intent(this, TestDatabaseActivity.class);
                startActivity(intent);
                return false;
            }else{
                NavigationUI.onNavDestinationSelected(item, navCo);
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        binding.bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}