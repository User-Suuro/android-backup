package com.example.filipinofoodreceipesredesign.pager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filipinofoodreceipesredesign.R;
import com.example.filipinofoodreceipesredesign.helpers.Assets;
import com.example.filipinofoodreceipesredesign.helpers.adapter.Recycler;
import com.example.filipinofoodreceipesredesign.models.RecipesModel;

import java.io.Serializable;
import java.util.List;


public class PagerContentFragment extends Fragment {
    private static final String ARG_RECIPES = "recipes_data";
    List<RecipesModel> RecipesData;
    RecyclerView recyclerView;
    Recycler<RecipesModel> adapter;

    public PagerContentFragment() {
        //required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static PagerContentFragment newInstance(List<RecipesModel> recipesData) {
        PagerContentFragment fragment = new PagerContentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPES, (Serializable) recipesData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_content, container, false);

        if (getArguments() != null) {
            RecipesData = (List<RecipesModel>) getArguments().getSerializable(ARG_RECIPES);
        }

        recyclerView = view.findViewById(R.id.recyclerContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Recycler<>(getContext(), RecipesData, R.layout.item_recycler_recipe, (itemView, item, position) -> {
            TextView textTitle = itemView.findViewById(R.id.title);
            TextView textDesc = itemView.findViewById(R.id.desc);
            ImageView imageSide = itemView.findViewById(R.id.image_food);

            textTitle.setText(item.getName());
            textDesc.setText(item.getDescription());
            Assets.loadImageFromAssets(getContext(), item.getRecipeUrl(), imageSide);
        });

        adapter.setOnItemClickListener((itemView, item, position) -> {
            // Handle item click
            Toast.makeText(getContext(), String.valueOf(item.getRecipeUrl()), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
        return view;
    }
}