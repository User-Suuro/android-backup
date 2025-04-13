package com.example.sariapp.app.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.sariapp.R;
import com.example.sariapp.utils.ui.Router;
import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.db.pocketbase.PBCrud;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCollection;
import com.example.sariapp.models.Users;
import com.example.sariapp.utils.ui.Dialog;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;


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

    private final PBAuth auth = PBAuth.getInstance();;
    EditText emailInput, passInput, confirmInput;
    TextInputLayout emailInputLayout, passInputLayout, confirmInputLayout;
    PBCrud<Users> crud = new PBCrud<>(Users.class, PBCollection.USERS.getName());

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

        emailInputLayout = view.findViewById(R.id.inputEmail);
        passInputLayout = view.findViewById(R.id.inputPassword);
        confirmInputLayout = view.findViewById(R.id.inputConfirm);

        emailInput = emailInputLayout.getEditText();
        passInput = passInputLayout.getEditText();
        confirmInput = confirmInputLayout.getEditText();

        Button registerBtn = view.findViewById(R.id.buttonRegister);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                String password = passInput.getText().toString().trim();
                String confirm = confirmInput.getText().toString().trim();

                Dialog.showLoading(getContext());
                checkAndHandleExistingUser(email, password, confirm);
            }
        });

        return view;
    }

    private void checkAndHandleExistingUser(String email, String password, String confirm) {
        crud.list("email", email, new PBCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject res = new JSONObject(result);
                    if (res.getJSONArray("items").length() > 0) {
                        JSONObject user = res.getJSONArray("items").getJSONObject(0);
                        boolean verified = user.optBoolean("verified", true); // fallback to true just in case
                        String id = user.optString("id");

                        if (!verified) {
                            deleteUser(id, () -> createUser(email, password, confirm));
                        } else {
                            Dialog.exitLoading();
                            emailInputLayout.setError("Email Already Exists. Please try logging in");
                        }

                    } else {
                        createUser(email, password, confirm);
                    }
                } catch (JSONException e) {
                    showError("Error parsing user data.");
                }
            }

            @Override
            public void onError(String error) {
                showError("Failed to check user: " + error);
            }
        });
    }

    private void deleteUser(String id, Runnable onSuccess) {
        crud.delete(id, new PBCallback() {
            @Override
            public void onSuccess(String result) {
                requireActivity().runOnUiThread(onSuccess);
            }

            @Override
            public void onError(String error) {
                showError("Failed to delete user: " + error);
            }
        });
    }

    private void showError(String message) {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                Dialog.exitLoading();
                Dialog.showError(getContext(), message, () -> {});
            });
        }
    }

    private void createUser(String email, String password, String confirm) {
        Users users = new Users.Builder()
                .email(email)
                .password(password)
                .confirmPassword(confirm)
                .build();

        crud.create(users, new PBCallback() {
            @Override
            public void onSuccess(String result) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Dialog.exitLoading();

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String userID = jsonResponse.optString("id", null);

                            // Navigate to success fragment
                            Router.getInstance(getParentFragmentManager())
                                    .switchFragment(VerifyFragment.newInstance(users.getEmail(), userID), false);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
    }

}