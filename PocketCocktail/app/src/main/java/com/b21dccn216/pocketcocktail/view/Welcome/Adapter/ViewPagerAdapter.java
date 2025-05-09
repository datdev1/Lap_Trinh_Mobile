package com.b21dccn216.pocketcocktail.view.Welcome.Adapter;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.b21dccn216.pocketcocktail.view.Welcome.Fragment.ChooseMeasurementFragment;
import com.b21dccn216.pocketcocktail.view.Welcome.Fragment.DiscoveryFragment;
import com.b21dccn216.pocketcocktail.view.Welcome.Fragment.ItTimeForFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private Bundle bundle;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                ItTimeForFragment hfm = new ItTimeForFragment();
                hfm.setArguments(bundle);
                return hfm;
            case 1:
                DiscoveryFragment tdf = new DiscoveryFragment();
                tdf.setArguments(bundle);
                return tdf;
            case 2:
                ChooseMeasurementFragment acf = new ChooseMeasurementFragment();
                acf.setArguments(bundle);
                return acf;
            default:
                ItTimeForFragment hfm1 = new ItTimeForFragment();
                hfm1.setArguments(bundle);
                return hfm1;
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}