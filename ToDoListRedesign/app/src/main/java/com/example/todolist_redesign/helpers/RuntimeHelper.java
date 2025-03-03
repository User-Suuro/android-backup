package com.example.todolist_redesign.helpers;

import android.app.Application;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RuntimeHelper extends Application {
    private String currPageName;
    private String prevPageName;
    private Fragment currFragment;
    private Fragment prevFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize global state
        currPageName = "";
        prevPageName = "";
        currFragment = null;
        prevFragment = null;
    }

    // Getters and setters for page names
    public String getCurrPageName() {
        return currPageName;
    }

    public void setCurrPageName(String currPageName) {
        this.currPageName = currPageName;
    }

    public String getPrevPageName() {
        return prevPageName;
    }

    public void setPrevPageName(String prevPageName) {
        this.prevPageName = prevPageName;
    }

    // Getters and setters for fragment references
    public Fragment getCurrFragment() {
        return currFragment;
    }

    public void setCurrFragment(Fragment currFragment) {
        this.currFragment = currFragment;
    }

    public Fragment getPrevFragment() {
        return prevFragment;
    }

    public void setPrevFragment(Fragment prevFragment) {
        this.prevFragment = prevFragment;
    }

    public void switchFragment(AppCompatActivity activity,
                               Fragment fragment,
                               int containerViewId,
                               boolean addToBackStack) {

        // Get the new fragment's name automatically
        String newFragmentName = fragment.getClass().getSimpleName();

        // Update global state: set previous state to the current state
        setPrevPageName(getCurrPageName());
        setPrevFragment(getCurrFragment());

        // Update current state with new fragment details
        setCurrPageName(newFragmentName);
        setCurrFragment(fragment);

        // Perform the fragment transaction
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void prevFragment(AppCompatActivity activity, int containerViewId, boolean addToBackStack) {
        if (getPrevFragment() != null) {
            switchFragment(activity, getPrevFragment(), containerViewId, addToBackStack);
        }
    }

}