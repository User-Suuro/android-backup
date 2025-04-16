package com.example.sariapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sariapp.R;
import com.example.sariapp.app.discover.DiscoverFragment;
import com.example.sariapp.app.home.HomeFragment;
import com.example.sariapp.app.more.MoreFragment;
import com.example.sariapp.utils.ui.Router;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // go to this activity after auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Router router = Router.getInstance(getSupportFragmentManager());
        router.setContainerId(R.id.main_container);
        router.switchFragment(new HomeFragment(), false);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            String current = Router.getInstance(getSupportFragmentManager()).currFragmentName();

            if(item.getItemId() == R.id.nav_home) {

                if (!current.equals(HomeFragment.class.getSimpleName())) {
                    Router.getInstance(getSupportFragmentManager())
                            .switchFragment(new HomeFragment(), false);
                }

               return true;
            } else if (item.getItemId() == R.id.nav_discover) {

                if (!current.equals(DiscoverFragment.class.getSimpleName())) {
                    Router.getInstance(getSupportFragmentManager())
                            .switchFragment(new HomeFragment(), false);
                }

                return true;
            } else if (item.getItemId() == R.id.nav_profile) {

                if (!current.equals(MoreFragment.class.getSimpleName())) {
                    Router.getInstance(getSupportFragmentManager())
                            .switchFragment(new HomeFragment(), false);
                }

                return true;
            }

            return false;
        });

    }
}