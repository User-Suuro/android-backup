package com.example.filipinofoodreceipesredesign;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.filipinofoodreceipesredesign.helpers.Router;
import com.example.filipinofoodreceipesredesign.helpers.db.local.Sqlite;
import com.example.filipinofoodreceipesredesign.helpers.db.local.Static;
import com.example.filipinofoodreceipesredesign.models.RecipesModel;
import com.example.filipinofoodreceipesredesign.pager.PagerMainFragment;

public class MainActivity extends AppCompatActivity {
    Router router;
    Sqlite<RecipesModel> recipesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        router = new Router(getSupportFragmentManager(), R.id.frame_container);
        if (savedInstanceState == null) {
            router.switchFragment(new PagerMainFragment(), false);
        }

        recipesDB = new Sqlite.Builder<>(this, RecipesModel.class).build();
        recipesDB.loadStaticData(Static.getData(this), false);
    }

    public Sqlite<RecipesModel> getRecipesDB() {
        return recipesDB;
    }
}