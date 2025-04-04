package com.example.sariapp.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Router {
    private final FragmentManager fragmentManager;
    private final int containerId;

    public Router(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    // Switch to a new fragment
    public void switchFragment(Fragment newFragment, boolean addToBackStack) {
        String tag = newFragment.getClass().getSimpleName(); // Get the class name as the tag

        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerId, newFragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    // Switch back to the previous fragment
    public Fragment prevFragment() {
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount > 0) {
            String previousTag = fragmentManager.getBackStackEntryAt(backStackCount - 1).getName();
            Fragment previousFragment = fragmentManager.findFragmentByTag(previousTag);

            fragmentManager.popBackStack();

            if (previousFragment != null) {
                fragmentManager.beginTransaction().show(previousFragment).commit();
            }

            return previousFragment; // can be used for passing additional param (e.g. return value in fragment)
        }

        return null;
    }

    // Get the name of the current fragment
    public String currFragmentName() {
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        return (currentFragment != null) ? currentFragment.getClass().getSimpleName() : "None";
    }

    // Get the name of the previous fragment
    public String prevFragmentName() {
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount > 0) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backStackCount - 1);
            String tag = backStackEntry.getName();

            return (tag != null) ? tag : "Unnamed Fragment"; // Handle null tag cases
        }

        return "None"; // No previous fragment exists
    }
}