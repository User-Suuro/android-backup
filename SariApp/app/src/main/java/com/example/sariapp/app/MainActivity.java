package com.example.sariapp.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.app.discover.DiscoverFragment;
import com.example.sariapp.app.home.HomeFragment;
import com.example.sariapp.app.more.MoreFragment;
import com.example.sariapp.models.Stores;
import com.example.sariapp.models.Users;
import com.example.sariapp.utils.db.pocketbase.PBCrud;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCollection;
import com.example.sariapp.utils.ui.Router;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private final Router router = Router.getInstance(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example usage of the PBCrud class
        PBCrud<Stores> store_crud = new PBCrud<>(getApplicationContext(), PBSession.getUserInstance(getApplicationContext()), Stores.class, PBCollection.STORES.getName());




        router.switchFragment(HomeFragment.newInstance(),false, R.id.main_activity_container); // Initial fragment
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);


        bottomNav.setOnItemSelectedListener(item -> {
            String current = router.currFragmentName();

            if (item.getItemId() == R.id.nav_home) {
                if (!current.equals(DiscoverFragment.class.getSimpleName())) {
                    router.switchFragment(HomeFragment.newInstance(),false, R.id.main_activity_container);
                }
                return true;

            } else if (item.getItemId() == R.id.nav_discover) {
                if (!current.equals(DiscoverFragment.class.getSimpleName())) {
                    router.switchFragment(DiscoverFragment.newInstance(),false, R.id.main_activity_container);
                }
                return true;

            } else if (item.getItemId() == R.id.nav_profile) {
                if (!current.equals(MoreFragment.class.getSimpleName())) {
                    router.switchFragment(MoreFragment.newInstance(),false, R.id.main_activity_container);
                }
                return true;
            }

            return false;
        });
    }
}
