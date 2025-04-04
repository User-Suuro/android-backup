package com.example.filipinofoodreceipesredesign.pager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.filipinofoodreceipesredesign.MainActivity;
import com.example.filipinofoodreceipesredesign.R;
import com.example.filipinofoodreceipesredesign.helpers.adapter.Pager;
import com.example.filipinofoodreceipesredesign.helpers.db.local.sql.Sqlite;
import com.example.filipinofoodreceipesredesign.models.RecipesModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class PagerMainFragment extends Fragment {
    private static final String ARG_DB_NAME = "recipes_db";
    private Pager pagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public PagerMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pager_main, container, false);

        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tab_layout);
        pagerAdapter = new Pager(this, viewPager);

        Sqlite<RecipesModel> recipesDB = new Sqlite(getContext(), RecipesModel.class);
        List<String> titleList = recipesDB.getUniqueValues("category");
        List<RecipesModel> allRecipes = recipesDB.getAll();

        viewPager.setAdapter(pagerAdapter);

        for (int i = 0; i < titleList.size(); i++) {
            String title_i = titleList.get(i);
            // we filter data base from title

            List<RecipesModel> dataContent = allRecipes.stream()
                    .filter(recipe -> recipe.getCategory().equals(title_i))
                    .collect(Collectors.toList());

            Toast.makeText(getContext(), dataContent.get(0).getRecipeUrl(), Toast.LENGTH_SHORT).show();

            PagerContentFragment fragment = PagerContentFragment.newInstance(dataContent);
            pagerAdapter.addFragment(fragment, title_i);
        }

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(pagerAdapter.getPageTitle(position));
        }).attach();

        return view;
    }
}