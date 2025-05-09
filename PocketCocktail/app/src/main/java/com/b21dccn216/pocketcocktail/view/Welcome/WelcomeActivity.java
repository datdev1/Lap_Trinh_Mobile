package com.b21dccn216.pocketcocktail.view.Welcome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.b21dccn216.pocketcocktail.view.Login.LoginActivity;
import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.view.Welcome.Adapter.ViewPagerAdapter;
import com.b21dccn216.pocketcocktail.view.Welcome.Helper.HorizontalFlipTransformation;
import com.b21dccn216.pocketcocktail.databinding.ActivityWelcomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WelcomeActivity extends AppCompatActivity{

    private ActivityWelcomeBinding binding;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setPageTransformer(new HorizontalFlipTransformation());
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.setOffscreenPageLimit(3);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();

        binding.btnNext.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem == 2) {
                // TODO: Navigate to next activity or fragment
                Toast.makeText(this, "Navigate to next activity or fragment", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1, true);
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem == 0) {
                return;
            } else {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1, true);
            }
        });

    }
}