package com.example.sariapp.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sariapp.R;
import com.example.sariapp.app.discover.DiscoverFragment;
import com.example.sariapp.app.home.HomeFragment;
import com.example.sariapp.app.more.MoreFragment;
import com.example.sariapp.models.Staffs;
import com.example.sariapp.models.Stores;

import com.example.sariapp.utils.db.pocketbase.PBCrud;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCollection;

import com.example.sariapp.utils.ui.Router;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private final Router router = new Router(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Must be first

        PBCrud<Stores> store_crud = new PBCrud<>(Stores.class, PBCollection.STORES.getName(), PBSession.getUserInstance(getApplicationContext()).getToken());

        router.switchFragment(HomeFragment.newInstance(), false, R.id.main_activity_container);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            String current = router.currFragmentName();

            if (item.getItemId() == R.id.nav_home) {
                if (!current.equals(HomeFragment.class.getSimpleName())) {
                    router.switchFragment(HomeFragment.newInstance(), false, R.id.main_activity_container);
                }
                return true;

            } else if (item.getItemId() == R.id.nav_discover) {
                if (!current.equals(DiscoverFragment.class.getSimpleName())) {
                    router.switchFragment(DiscoverFragment.newInstance(), false, R.id.main_activity_container);
                }
                return true;

            } else if (item.getItemId() == R.id.nav_profile) {
                if (!current.equals(MoreFragment.class.getSimpleName())) {
                    router.switchFragment(MoreFragment.newInstance(), false, R.id.main_activity_container);
                }
                return true;
            }

            return false;
        });
    }

}

