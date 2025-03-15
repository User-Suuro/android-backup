package com.example.filipinofoodreceipesredesign.helpers.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class Pager extends FragmentStateAdapter {

    private final List<Fragment> fragments;
    private final List<String> fragmentTitles;

    // Constructor for FragmentActivity
    public Pager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragments = new ArrayList<>();
        this.fragmentTitles = new ArrayList<>();
    }

    // Constructor for Fragment (useful for nested ViewPager)
    public Pager(@NonNull Fragment fragment) {
        super(fragment);
        this.fragments = new ArrayList<>();
        this.fragmentTitles = new ArrayList<>();
    }

    // Constructor for FragmentManager & Lifecycle (more flexible)
    public Pager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragments = new ArrayList<>();
        this.fragmentTitles = new ArrayList<>();
    }

    // Constructor with predefined list of fragments & titles
    public Pager(@NonNull FragmentActivity fragmentActivity, @NonNull List<Fragment> fragments, @NonNull List<String> titles) {
        super(fragmentActivity);
        this.fragments = new ArrayList<>(fragments);
        this.fragmentTitles = new ArrayList<>(titles);
    }

    public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    public String getPageTitle(int position) {
        return (position >= 0 && position < fragmentTitles.size()) ? fragmentTitles.get(position) : "";
    }

    public void clearFragments() {
        fragments.clear();
        fragmentTitles.clear();
        notifyDataSetChanged();
    }
}
