package com.b21dccn216.pocketcocktail.view.Login.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.b21dccn216.pocketcocktail.view.Login.fragment.LoginFragment;
import com.b21dccn216.pocketcocktail.view.Login.fragment.SignUpFragment;

public class LoginViewPagerAdapter extends FragmentStateAdapter {

    public LoginViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new LoginFragment();
        }else if(position == 1){
            return new SignUpFragment();
        }else{
            return new LoginFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}

