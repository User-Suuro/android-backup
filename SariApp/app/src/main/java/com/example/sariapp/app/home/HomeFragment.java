package com.example.sariapp.app.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sariapp.R;
import com.example.sariapp.models.Staffs;
import com.example.sariapp.models.Stores;
import com.example.sariapp.models.Users;
import com.example.sariapp.utils.adapter.Pager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private Pager pager;
    private TabLayout tabLayout;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewpager);
        pager = new Pager(this, viewPager);
        tabLayout = view.findViewById(R.id.tab_layout);

        List<Stores> storesList = new ArrayList<>();
        List<Staffs> staffList = new ArrayList<>();

        pager.addFragment(RecyclerStoreStaffFragment.newInstance(storesList, Stores.class), "Stores");
        pager.addFragment(RecyclerStoreStaffFragment.newInstance(staffList, Staffs.class), "Organization");

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pager.getPageTitle(position)));
        mediator.attach();

        return view;
    }
}