package com.example.sariapp.app.auth;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.ui.Dialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "user_email";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public VerifyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyFragment newInstance(String param1) {
        VerifyFragment fragment = new VerifyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify, container, false);
        TextView emailTextView = view.findViewById(R.id.user_email_placeholder);
        emailTextView.setText(mParam1);

        // Inflate the loading and error views
        View loadingView = inflater.inflate(R.layout.dialog_loading, null);
        View errorView = inflater.inflate(R.layout.dialog_error, null);

        // Show loading dialog
        AlertDialog loadingDialog = Dialog.showLoading(requireActivity(), loadingView);

        // Verify user
        PBAuth.getInstance().requestOTP(mParam1, new PBAuth.Callback() {
            @Override
            public void onSuccess(String result) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        // Dismiss loading dialog
                        Dialog.exitLoading(loadingDialog);
                        Toast.makeText(getContext(), "Verification successful!", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        // Dismiss loading dialog
                        Dialog.exitLoading(loadingDialog);

                        // Show error dialog with the error message
                        Dialog.showError(requireActivity(), errorView, "Verification failed: " + error, () -> {
                            // Optional: retry or exit logic
                            Toast.makeText(getContext(), "Retrying...", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            }
        });

        return view;
    }
}