package com.example.filipinofoodreceipesredesign.pager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filipinofoodreceipesredesign.R;
import com.example.filipinofoodreceipesredesign.models.RecipesModel;

import java.util.List;


public class ContentFragment extends Fragment {

    List<RecipesModel> RecipesData;
    public ContentFragment(List<RecipesModel> RecipesData) {
        this.RecipesData = RecipesData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        return view;
    }
}