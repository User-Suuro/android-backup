package com.example.sariapp.app.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sariapp.R;

public class DashboardActivity extends AppCompatActivity {
    // go to this activity after auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}