package com.example.todolist_redesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.todolist_redesign.helpers.RuntimeHelper;
import com.example.todolist_redesign.ui.home.CategoriesFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RuntimeHelper runtimeHelper = new RuntimeHelper();

        runtimeHelper.switchFragment(this, new CategoriesFragment(),
                                            R.id.fragment_container, true);
    }
}