package com.example.project.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project.fragment.FriendFragment;
import com.example.project.fragment.InformationFragment;
import com.example.project.fragment.MessFragment;

public class FragmentAdapter extends FragmentStateAdapter {


    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new FriendFragment();

        }
        return new MessFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
