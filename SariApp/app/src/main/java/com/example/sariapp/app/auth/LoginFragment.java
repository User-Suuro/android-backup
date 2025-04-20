package com.example.sariapp.app.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.app.MainActivity;
import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.ui.Dialog;
import com.example.sariapp.utils.ui.Router;
import com.example.sariapp.models.Users;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    EditText emailInput, passInput;
    TextInputLayout emailLayout, passLayout;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailLayout = view.findViewById(R.id.inputEmail);
        passLayout = view.findViewById(R.id.inputPassword);

        emailInput = emailLayout.getEditText();
        passInput = passLayout.getEditText();

        Button btnLogin = view.findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                String password = passInput.getText().toString().trim();
                login(email, password);
            }
        });

        TextView goLoginText = view.findViewById(R.id.clickableRegisterLabel);
        goLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router router = new Router(requireActivity().getSupportFragmentManager());
                router.switchFragment(new SignupFragment(), false, R.id.auth_container);
            }
        });

        return view;
    }

    private void login(String email, String password) {
        PBAuth auth = PBAuth.getInstance();
        Dialog.showLoading(getContext());

        // Call loginUser method from PBAuth to authenticate the user
        auth.loginUser(email, password, new PBCallback() {
            @Override
            public void onSuccess(String result) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            // Parse the JSON response
                            JSONObject jsonResponse = new JSONObject(result);
                            String token = jsonResponse.optString("token");
                            JSONObject record = jsonResponse.getJSONObject("record");

                            // Build the Users model using the Builder pattern
                            Users user = new Users.Builder()
                                    .id(record.optString(Users.Fields.ID))
                                    .name(jsonResponse.optString(Users.Fields.NAME))
                                    .email(jsonResponse.optString(Users.Fields.EMAIL))
                                    .tokenKey(token)
                                    .build();

                            // Save the token and the Users object to PBSession singleton
                            PBSession.getUserInstance(getContext()).setUser(user);  // Save Users object

                            Dialog.exitLoading();

                            // Navigate to MainActivity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing errors
                            showError("Failed to parse the login response.");
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        showError(errorMessage);
                    });
                }
            }
        });
    }

    private void showError(String message) {
        // Display an error message, for example, using a Toast
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
