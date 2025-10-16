package com.android_assignments.expensetrackerpro_maan.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android_assignments.expensetrackerpro_maan.fragments.AllTransactionsFragment;
import com.android_assignments.expensetrackerpro_maan.fragments.FavoritesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllTransactionsFragment();
            case 1:
                return new FavoritesFragment();
            default:
                return new AllTransactionsFragment(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
