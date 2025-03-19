package com.example.filipinofoodreceipesredesign.pager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filipinofoodreceipesredesign.R;
import com.example.filipinofoodreceipesredesign.helpers.adapter.Pager;
import com.google.android.material.tabs.TabLayout;


public class PagerMainFragment extends Fragment {
    private Pager pagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    public PagerMainFragment() {
        // Required empty public constructor
    }

    public static PagerMainFragment newInstance(String param1, String param2) {
        PagerMainFragment fragment = new PagerMainFragment();
        return fragment;
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


        // List<Fragment> fragmentList = Arrays.asList(new RecipesFragment(), new ContentFragment());
        // List<String> titleList = Arrays.asList("Recipes", "Desserts");

        // pagerAdapter = new PagerAdapter(requireActivity(), fragmentList, titleList);
       //  viewPager.setAdapter(pagerAdapter);

        return view;
    }
}