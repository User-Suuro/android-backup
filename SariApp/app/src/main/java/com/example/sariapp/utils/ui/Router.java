package com.example.sariapp.utils.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Router {
    private final FragmentManager fragmentManager;
    private Integer containerId = null;

    public Router(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void switchFragment(Fragment newFragment, boolean addToBackStack, int containerId) {
        this.containerId = containerId;

        String tag = newFragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerId, newFragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    public Fragment prevFragment() {
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0) {
            String previousTag = fragmentManager.getBackStackEntryAt(backStackCount - 1).getName();
            Fragment previousFragment = fragmentManager.findFragmentByTag(previousTag);
            fragmentManager.popBackStack();

            if (previousFragment != null) {
                fragmentManager.beginTransaction().show(previousFragment).commit();
            }

            return previousFragment;
        }
        return null;
    }

    public String currFragmentName() {
        if (containerId == null) {
            return "Unknown";
        }

        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        return (currentFragment != null) ? currentFragment.getClass().getSimpleName() : "None";
    }

    public String prevFragmentName() {
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(backStackCount - 1);
            return (entry.getName() != null) ? entry.getName() : "Unnamed Fragment";
        }
        return "None";
    }
}
