package com.example.filipinofoodreceipesredesign.helpers.route;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentHelper {
    private final FragmentManager fragmentManager;
    private final int containerId;

    public FragmentHelper(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    // Switch to a new fragment
    public void switchFragment(Fragment newFragment, boolean addToBackStack, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerId, newFragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    // Switch back to the previous fragment
    public boolean switchToPreviousFragment() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        }
        return false; // No previous fragment to switch to
    }

    // Get the name of the current fragment
    public String getCurrentFragmentName() {
        Fragment currentFragment = fragmentManager.findFragmentById(containerId);
        return (currentFragment != null) ? currentFragment.getClass().getSimpleName() : "None";
    }

    // Get the name of the previous fragment
    public String getPreviousFragmentName() {
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backStackCount - 1);
            return backStackEntry.getName();
        }
        return "None";
    }
}