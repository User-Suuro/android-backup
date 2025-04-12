package com.example.sariapp.app.auth;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.utils.Router;
import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.db.pocketbase.PBCrud;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCollection;
import com.example.sariapp.models.User;
import com.example.sariapp.utils.ui.Dialog;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        TextInputLayout emailInputLayout = view.findViewById(R.id.inputEmail);
        TextInputLayout passInputLayout = view.findViewById(R.id.inputPassword);
        TextInputLayout confirmInputLayout = view.findViewById(R.id.inputConfirm);

        EditText emailInput = emailInputLayout.getEditText();
        EditText passInput = passInputLayout.getEditText();
        EditText confirmInput = confirmInputLayout.getEditText();

        Button registerBtn = view.findViewById(R.id.buttonRegister);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                String password = passInput.getText().toString().trim();
                String confirm = confirmInput.getText().toString().trim();

                User user = new User.Builder()
                        .email(email)
                        .password(password)
                        .confirmPassword(confirm)
                        .build();

                PBCrud<User> registerUser = new PBCrud<>(User.class,
                        PBAuth.getInstance(),
                        PBCollection.USERS.getName(),
                        null
                );

                FrameLayout container = ((AuthActivity) requireActivity()).getAuthContainer();
                View loadingView = inflater.inflate(R.layout.dialog_loading, null);
                View errorView = inflater.inflate(R.layout.dialog_error, null);
                AlertDialog loadingDialog = Dialog.showLoading(requireActivity(), loadingView);

                registerUser.create(user, new PBCrud.Callback() {
                    @Override
                    public void onSuccess(String result) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                Dialog.exitLoading(loadingDialog);

                                // Navigate to success fragment
                                Router.getInstance(getParentFragmentManager(), container.getId())
                                        .switchFragment(VerifyFragment.newInstance(user.getEmail()), false);
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                Dialog.exitLoading(loadingDialog);

                                // Show error with retry or exit
                                Dialog.showError(requireActivity(), errorView, "Failed to register: " + error, () -> {
                                    // Optional: navigate back or retry
                                    Toast.makeText(requireContext(), "Retry clicked", Toast.LENGTH_SHORT).show();
                                });
                            });
                        }
                    }
                });
            }
        });

        return view;
    }
}