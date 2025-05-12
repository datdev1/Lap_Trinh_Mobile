package com.b21dccn216.pocketcocktail.view.Main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.TestDatabaseActivity;
import com.b21dccn216.pocketcocktail.view.Login.LoginActivity;
import com.b21dccn216.pocketcocktail.view.Login.LoginContract;
import com.b21dccn216.pocketcocktail.view.Main.adapter.CocktailHomeItemAdapter;
import com.b21dccn216.pocketcocktail.databinding.ActivityHomeBinding;
import com.b21dccn216.pocketcocktail.view.CreateDrink.CreateDrinkActivity;
import com.b21dccn216.pocketcocktail.view.Search.SearchActivity;
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

public class HomeActivity extends BaseAppCompatActivity<BaseContract.View, BaseContract.Presenter> {
    private ActivityHomeBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected BaseContract.Presenter createPresenter() {
        return new BaseContract.Presenter<BaseContract.View>() {
            private BaseContract.View view;

            @Override
            public void attachView(BaseContract.View view) {
                this.view = view;
            }

            @Override
            public void detachView() {
                this.view = null;
            }

            @Override
            public void onCreate() {
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onResume() {
            }

            @Override
            public void onPause() {
            }

            @Override
            public void onStop() {
            }

            @Override
            public void onDestroy() {
            }
        };
    }

    @Override
    protected BaseContract.View getView() {
        return this;
    }

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
        //setSupportActionBar(binding.toolbar);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_discover, R.id.nav_favorite, R.id.nav_profile)
                .build();

        //bottom navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        navController = navHostFragment.getNavController();

        // Bind toolbar & bottomNav with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        // Handle admin tab
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            if(item.getItemId() == R.id.nav_admin){
                Intent intent = new Intent(this, TestDatabaseActivity.class);
                startActivity(intent);
                return false;
            }else{
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
        });
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.nav_add){
                    Intent intent = new Intent(HomeActivity.this, CreateDrinkActivity.class);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.nav_search){
                    Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        binding.bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}