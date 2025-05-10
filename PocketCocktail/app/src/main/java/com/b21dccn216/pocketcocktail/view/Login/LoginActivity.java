package com.b21dccn216.pocketcocktail.view.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.Login.adapter.LoginViewPagerAdapter;
import com.b21dccn216.pocketcocktail.databinding.ActivityLoginBinding;
import com.b21dccn216.pocketcocktail.view.Main.HomeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity{

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(false){      //this line for debug only
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserDAO userDAO = new UserDAO();
            userDAO.getUserByUuidAuthen(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    querySnapshot -> {
                        if(querySnapshot.getDocuments().isEmpty()){
                            return;
                        }
                        User u = querySnapshot.getDocuments().get(0).toObject(User.class);
                        if(u == null){
                            return;
                        }
                        SessionManager.getInstance().setUser(u);
                        // User is logged in, go to MainActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // close LoginActivity
                        return;
                    }, e -> {
                        // TODO: process error
                        return;
                    });
        }

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