package com.example.filipinofoodreceipesredesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.filipinofoodreceipesredesign.helpers.Router;
import com.example.filipinofoodreceipesredesign.helpers.db.local.Static;
import com.example.filipinofoodreceipesredesign.helpers.db.local.sql.Sqlite;
import com.example.filipinofoodreceipesredesign.models.RecipesModel;
import com.example.filipinofoodreceipesredesign.pager.PagerContentFragment;
import com.example.filipinofoodreceipesredesign.pager.PagerMainFragment;

public class MainActivity extends AppCompatActivity {
    Router router;
    Sqlite<RecipesModel> recipesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Filipino Food Recipes");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        router = new Router(getSupportFragmentManager(), R.id.frame_container);
        recipesDB = new Sqlite(this, RecipesModel.class);
        recipesDB.loadStaticData(Static.getData(this));

        Toast.makeText(this, recipesDB.getAll().get(0).getRecipeUrl(), Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            router.switchFragment(new PagerMainFragment(), false);
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        recipesDB.close();
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Action bar event
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }
}

