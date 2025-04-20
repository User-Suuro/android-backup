package com.example.sariapp.app.home.stores;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.models.Stores;
import com.example.sariapp.utils.db.pocketbase.PBCrud;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCollection;
import com.example.sariapp.utils.ui.Dialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateStoreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters


    public CreateStoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment CreateStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateStoreFragment newInstance() {
        CreateStoreFragment fragment = new CreateStoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_store, container, false);

        TextInputLayout layoutStoreName = view.findViewById(R.id.inputStoreName);
        TextInputLayout layoutStoreDescription = view.findViewById(R.id.inputStoreDescription);
        TextInputLayout layoutStoreAddress = view.findViewById(R.id.inputStoreAddress);
        TextInputLayout layoutDoE = view.findViewById(R.id.inputDateEstablishment);
        Button btnCreateStore = view.findViewById(R.id.btnCreateStore);

        EditText etStoreName = layoutStoreName.getEditText();
        EditText etStoreDescription = layoutStoreDescription.getEditText();
        EditText etStoreAddress = layoutStoreAddress.getEditText();
        EditText etDoE = layoutDoE.getEditText();

        etDoE.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date_of_establishment))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getParentFragmentManager(), "doe_picker");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.mm_dd_yyyy), Locale.getDefault());
                String formattedDate = sdf.format(new Date(selection));
                etDoE.setText(formattedDate);
            });
        });

        btnCreateStore.setOnClickListener(v-> {
            String storeName = etStoreName.getText().toString();
            String storeDescription = etStoreDescription.getText().toString();
            String storeAddress = etStoreAddress.getText().toString();
            String dateOfEstablishment = etDoE.getText().toString();

            // Checkers

            if (storeName.isEmpty()) {
                layoutStoreName.setError(getString(R.string.required_field));
                return;
            }

            if (storeDescription.isEmpty()) {
                layoutStoreDescription.setError(getString(R.string.required_field));
                return;
            }

            if (storeAddress.isEmpty()) {
                layoutStoreAddress.setError(getString(R.string.required_field));
                return;
            }

            if (dateOfEstablishment.isEmpty()) {
                layoutDoE.setError(getString(R.string.required_field));
                return;
            }

            PBCrud<Stores> stores_crud = new PBCrud<>(Stores.class, PBCollection.STORES.getName(), PBSession.getUserInstance(getContext()).getToken());
            Stores store = new Stores.Builder()
                    .name(storeName)
                    .owner(PBSession.getUserInstance(getContext()).getUserId())
                    .description(storeDescription)
                    .address(storeAddress)
                    .establishment(dateOfEstablishment)
                    .build();

            Dialog.showLoading(getContext());

            stores_crud.create(store, new PBCallback() {
                @Override
                public void onSuccess(String result) {
                    // success
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Dialog.exitLoading();
                            Toast.makeText(getContext(), "Store Successfully Added", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    // error
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Dialog.exitLoading();
                            Dialog.showError(getContext(), error, null);
                        });
                    }

                }
            });
        });


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show back button in action bar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Show back button
            actionBar.setTitle(getString(R.string.join_store)); // Optional: set title
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Restore the default action bar when this fragment is gone
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false); // Hide back button
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed(); // Handle back press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}