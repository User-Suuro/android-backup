package com.example.sariapp.app.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sariapp.R;
import com.example.sariapp.models.Staffs;
import com.example.sariapp.models.Stores;
import com.example.sariapp.models.Users;
import com.example.sariapp.utils.adapter.Pager;
import com.example.sariapp.utils.db.pocketbase.PBCrud;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCollection;
import com.example.sariapp.utils.misc.FadePageTransformer;
import com.example.sariapp.utils.ui.Dialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private Pager pager;
    private TabLayout tabLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tab_layout);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(getString(R.string.home));

        pager = new Pager(this, viewPager);
        viewPager.setPageTransformer(new FadePageTransformer());
        viewPager.setAdapter(pager); // ✅ Set adapter before anything else

        List<Stores> storesList = new ArrayList<>();
        List<Staffs> staffList = new ArrayList<>();

        String token = PBSession.getUserInstance(getContext()).getToken();
        String userId = PBSession.getUserInstance(getContext()).getUser().getID();

        PBCrud<Stores> stores_crud = new PBCrud<>(Stores.class, PBCollection.STORES.getName(), token);
        PBCrud<Staffs> staffs_crud = new PBCrud<>(Staffs.class, PBCollection.STAFFS.getName(), token);

        Dialog.showLoading(getContext());

        stores_crud.listBuilder()
                .filter("owner=\"" + userId + "\"")
                .expand("owner") // <--- this is the key!
                .execute(new PBCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Dialog.exitLoading();
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                try {
                                    JSONObject json = new JSONObject(result);
                                    JSONArray items = json.getJSONArray("items");

                                    List<Stores> storesList = new ArrayList<>();
                                    List<Staffs> staffList = new ArrayList<>(); // If you're using this for tabs

                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.getJSONObject(i);

                                        Stores store = new Stores.Builder()
                                                .setId("id")
                                                .setName(item.optString("name"))
                                                .setOwner(item.optString("owner"))
                                                .setDescription(item.optString("description"))
                                                .setAddress(item.optString("address"))
                                                .setEstablishmentDate(item.optString("establishment"))
                                                .setCreated(item.optString("created"))
                                                .build();

                                        storesList.add(store);
                                    }

                                    // ⚡ Use the parsed list
                                    pager.addFragment(RecyclerStoreStaffFragment.newInstance(storesList, Stores.class), getString(R.string.stores));
                                    pager.addFragment(RecyclerStoreStaffFragment.newInstance(staffList, Staffs.class), getString(R.string.organization));

                                    new TabLayoutMediator(tabLayout, viewPager,
                                            (tab, position) -> tab.setText(pager.getPageTitle(position))
                                    ).attach();

                                } catch (JSONException e) {
                                    Log.e("hatdog_json_error", "Parsing failed: " + e.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Dialog.exitLoading();
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                Log.d("hatdog_error", error);
                            });
                        }
                    }
                });


        return view;
    }
}
