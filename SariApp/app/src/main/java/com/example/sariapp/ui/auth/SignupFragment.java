package com.example.sariapp.ui.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.helpers.db.pocketbase.PBConn;
import com.example.sariapp.helpers.db.pocketbase.PBCrud;
import com.example.sariapp.helpers.db.pocketbase.PBTypes.PBCollection;
import com.example.sariapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


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

        EditText emailInput = view.findViewById(R.id.inputEmail);
        EditText passInput = view.findViewById(R.id.inputPassword);
        EditText confirmInput = view.findViewById(R.id.inputConfirm);

        Button registerBtn = view.findViewById(R.id.buttonRegister);

        User user = new User.Builder().email(emailInput.toString()).password(passInput.toString()).confirmPassword(confirmInput.toString()).build();
        PBConn pb = PBConn.getInstance();
        PBCrud<User> userCRUD = new PBCrud<>(User.class, pb.getClient(), pb.getBaseUrl(), PBCollection.USERS.getName(), pb.getToken());

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

                PBConn pb = PBConn.getInstance();
                PBCrud<User> userCRUD = new PBCrud<>(User.class, pb.getClient(), pb.getBaseUrl(), PBCollection.USERS.getName(), pb.getToken());

                userCRUD.create(user, new PBCrud.Callback() {
                    @Override
                    public void onSuccess(String result) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "Success: " + result, Toast.LENGTH_LONG).show()
                            );
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_LONG).show()
                            );
                        }
                    }
                });
            }
        });



        return view;
    }



}