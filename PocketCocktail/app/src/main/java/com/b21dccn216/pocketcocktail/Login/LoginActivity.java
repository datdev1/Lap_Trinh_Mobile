package com.b21dccn216.pocketcocktail.Login;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.b21dccn216.pocketcocktail.Login.Adapter.LoginViewPagerAdapter;
import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ActivityLoginBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginViewPagerAdapter viewPagerAdapter = new LoginViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, pos) -> {
           if(pos == 0) tab.setText("Login");
           else if (pos == 1) tab.setText("Sign up");
           else tab.setText("Login");
        }).attach();

    }
}