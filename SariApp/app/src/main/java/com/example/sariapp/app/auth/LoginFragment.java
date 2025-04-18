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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText emailInput, passInput;
    TextInputLayout emailLayout, passLayout;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                Router.getInstance(getFragmentManager()).switchFragment(new SignupFragment(), false);
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
                try {
                    // Get the token from the response (assuming it's in the response)
                    JSONObject jsonResponse = new JSONObject(result);
                    String token = jsonResponse.optString("token");

                    // Save the token to PBAuth singleton
                    PBSession.getInstance().setToken(token);
                    PBSession.getInstance().setRecord(jsonResponse.getJSONObject("record"));

                    Dialog.exitLoading();

                    // Redirect to MainActivity (or wherever you want to go post-login)
                    navigateToMainActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle any issues with the response parsing
                    showError("Failed to parse the login response.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error, maybe show a Toast or update the UI
                showError(errorMessage);
            }
        });
    }

    private void showError(String message) {
        // Display an error message, for example, using a Toast
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Optional: To close LoginActivity if necessary
    }

}