package com.example.sariapp.helpers.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class Pager extends FragmentStateAdapter {

    private final List<Fragment> fragments;
    private final List<String> fragmentTitles;
    private final ViewPager2 viewPager;

    // Constructor for FragmentActivity
    public Pager(@NonNull FragmentActivity fragmentActivity, ViewPager2 viewPager) {
        super(fragmentActivity);
        this.fragments = new ArrayList<>();
        this.fragmentTitles = new ArrayList<>();
        this.viewPager = viewPager;
    }

    // Constructor for Fragment (useful for nested ViewPager)
    public Pager(@NonNull Fragment fragment, ViewPager2 viewPager) {
        super(fragment);
        this.fragments = new ArrayList<>();
        this.fragmentTitles = new ArrayList<>();
        this.viewPager = viewPager;
    }

    // Constructor for FragmentManager & Lifecycle (more flexible)
    public Pager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ViewPager2 viewPager) {
        super(fragmentManager, lifecycle);
        this.fragments = new ArrayList<>();
        this.fragmentTitles = new ArrayList<>();
        this.viewPager = viewPager;
    }

    // Constructor with predefined list of fragments & titles
    public Pager(@NonNull FragmentActivity fragmentActivity, @NonNull List<Fragment> fragments, @NonNull List<String> titles, ViewPager2 viewPager) {
        super(fragmentActivity);
        this.fragments = new ArrayList<>(fragments);
        this.fragmentTitles = new ArrayList<>(titles);
        this.viewPager = viewPager;
    }

    public void addFragment(@NonNull Fragment fragment, @NonNull String title) {
        fragments.add(fragment);
        fragmentTitles.add(title);
        notifyItemInserted(fragments.size() - 1);
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
        int oldSize = fragments.size();
        fragments.clear();
        fragmentTitles.clear();
        notifyItemRangeRemoved(0, oldSize);
    }

    // Ensure correct updates for ViewPager2
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < fragments.size();
    }

    // Get current index of ViewPager2
    public int getCurrentIndex() {
        return (viewPager != null) ? viewPager.getCurrentItem() : -1;
    }
}
