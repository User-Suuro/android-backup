package com.example.sariapp.app.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sariapp.R;
import com.example.sariapp.models.Staffs;
import com.example.sariapp.models.Stores;
import com.example.sariapp.utils.adapter.Recycler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class RecyclerStoreStaffFragment<T> extends Fragment {

    private static final String ARG_DATA = "ARG_DATA";
    private List<T> argData;
    private Class<T> type;

    public RecyclerStoreStaffFragment(Class<T> type) {
        this.type = type;
    }

    public static <T> RecyclerStoreStaffFragment<T> newInstance(List<T> data, Class<T> type) {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        RecyclerStoreStaffFragment<T> fragment = new RecyclerStoreStaffFragment<>(type);
        Bundle args = new Bundle();
        args.putString(ARG_DATA, json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String json = getArguments().getString(ARG_DATA);
            Type listType = TypeToken.getParameterized(List.class, type).getType();
            argData = new Gson().fromJson(json, listType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_store_staff, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Recycler<T> adapter = new Recycler<>(
                requireContext(),
                argData,
                R.layout.recycler_item_store_staff, // Use your actual layout file
                (itemView, item, position) -> {
                    // Example binding logic
                    // You can do instanceof check to customize based on class

                    if (item instanceof Stores) {

                    } else if (item instanceof Staffs) {

                    }
                }
        );

        recyclerView.setAdapter(adapter);

        return view;
    }
}
