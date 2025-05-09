package com.b21dccn216.pocketcocktail.view.Login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.view.Login.adapter.LoginViewPagerAdapter;
import com.b21dccn216.pocketcocktail.databinding.ActivityLoginBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoginActivity extends AppCompatActivity{

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
    }

    private void initView(){
        LoginViewPagerAdapter viewPagerAdapter = new LoginViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, pos) -> {
            if(pos == 0) tab.setText("Login");
            else if (pos == 1) tab.setText("Sign up");
            else tab.setText("Login");
//           binding.viewPager.
        }).attach();
    }
}