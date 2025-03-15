package com.example.filipinofoodreceipesredesign;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.filipinofoodreceipesredesign.helpers.db.local.Runtime;
import com.example.filipinofoodreceipesredesign.pager.PagerMainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Runtime.getInstance().switchFragment(this, new PagerMainFragment(), R.id.frame_container, true);
    }
}