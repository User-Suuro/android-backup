package com.example.sariapp.app.home.stores;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateStoreFragment extends Fragment {

    public CreateStoreFragment() {
        // Required empty public constructor
    }

    public static CreateStoreFragment newInstance() {
        return new CreateStoreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        if (etStoreName == null || etStoreDescription == null || etStoreAddress == null || etDoE == null) {
            Toast.makeText(getContext(), "Error loading input fields", Toast.LENGTH_SHORT).show();
            return view;
        }

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

        btnCreateStore.setOnClickListener(v -> {
            // Clear previous errors
            layoutStoreName.setError(null);
            layoutStoreDescription.setError(null);
            layoutStoreAddress.setError(null);
            layoutDoE.setError(null);

            String storeName = etStoreName.getText().toString().trim();
            String storeDescription = etStoreDescription.getText().toString().trim();
            String storeAddress = etStoreAddress.getText().toString().trim();
            String dateOfEstablishment = etDoE.getText().toString().trim();

            boolean hasError = false;

            if (storeName.isEmpty()) {
                layoutStoreName.setError(getString(R.string.required_field));
                hasError = true;
            }

            if (storeDescription.isEmpty()) {
                layoutStoreDescription.setError(getString(R.string.required_field));
                hasError = true;
            }

            if (storeAddress.isEmpty()) {
                layoutStoreAddress.setError(getString(R.string.required_field));
                hasError = true;
            }

            if (dateOfEstablishment.isEmpty()) {
                layoutDoE.setError(getString(R.string.required_field));
                hasError = true;
            }

            if (hasError) return;

            PBCrud<Stores> stores_crud = new PBCrud<>(Stores.class, PBCollection.STORES.getName(), PBSession.getUserInstance(getContext()).getToken());

            Stores store = new Stores.Builder()
                    .setName(storeName)
                    .setOwner(PBSession.getUserInstance(getContext()).getUser().getId())
                    .setDescription(storeDescription)
                    .setAddress(storeAddress)
                    .setEstablishmentDate(dateOfEstablishment)
                    .build();


            Dialog.showLoading(getContext());

            stores_crud.create(store, new PBCallback() {
                @Override
                public void onSuccess(String result) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Dialog.exitLoading();
                            Toast.makeText(getContext(), "Store successfully added", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack(); // Navigate back
                        });
                    }
                }

                @Override
                public void onError(String error) {
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
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.create_store));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
