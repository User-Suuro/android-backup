package com.example.sariapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Router {
    private static volatile Router instance;
    private FragmentManager fragmentManager;
    private int containerId;


    // Private constructor to prevent instantiation
    public Router(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }


    // Method to get the singleton instance
    public static Router getInstance(FragmentManager fragmentManager, int containerId) {
        if (instance == null) {  // First check (performance optimization)
            synchronized (Router.class) {
                if (instance == null) {  // Second check (ensure thread safety)
                    instance = new Router(fragmentManager, containerId);
                }
            }
        }
        return instance;
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

            return previousFragment;
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

            return (tag != null) ? tag : "Unnamed Fragment";
        }

        return "None";
    }

    // Method to reset the state (clear internal fields)
    public static void clear() {
        if (instance != null) {
            instance.fragmentManager = null;  // Clear the fragment manager reference
            instance.containerId = 0;  // Reset container ID
            instance = null;  // Nullify the instance to allow for garbage collection
        }
    }
}
