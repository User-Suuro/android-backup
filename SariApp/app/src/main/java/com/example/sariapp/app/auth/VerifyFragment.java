package com.example.sariapp.app.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.sariapp.R;
import com.example.sariapp.utils.ui.Router;
import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.ui.Dialog;
import com.example.sariapp.utils.ui.Otp;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyFragment extends Fragment {

    private static final String ARG_PARAM1 = "user_email";
    private static final String ARG_PARAM2 = "user_id";
    private static final String TAG = "VerifyFragment";
    private String mParam1;
    private String mParam2;
    private String otpId;

    private Button resendButton;
    private TextView resendLabel;
    private boolean otpProcessed = false;
    private boolean isOtpSent = false;  // New flag to track OTP sent state


    private EditText[] otpFields;
    private Otp sharedOtpWatcher;

    public VerifyFragment() {}

    public static VerifyFragment newInstance(String param1, String param2) {
        VerifyFragment fragment = new VerifyFragment();
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

        // Restore state if available
        if (savedInstanceState != null) {
            isOtpSent = savedInstanceState.getBoolean("isOtpSent", false);
            otpId = savedInstanceState.getString("otpID", null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify, container, false);

        TextView emailTextView = view.findViewById(R.id.user_email_placeholder);
        emailTextView.setText(mParam1);

        TextInputLayout[] inputLayouts = new TextInputLayout[]{
                view.findViewById(R.id.otpInput01),
                view.findViewById(R.id.otpInput02),
                view.findViewById(R.id.otpInput03),
                view.findViewById(R.id.otpInput04),
                view.findViewById(R.id.otpInput05),
                view.findViewById(R.id.otpInput06)
        };

        otpFields = new EditText[inputLayouts.length];
        for (int i = 0; i < inputLayouts.length; i++) {
            otpFields[i] = inputLayouts[i].getEditText();
        }

        sharedOtpWatcher = new Otp(otpFields, otp -> {
            if (otpProcessed) {
                Log.w(TAG, "OTP already processed, skipping...");
                return;
            }

            otpProcessed = true;
            Log.d(TAG, "Processing OTP: " + otp);
            verifyOTP(otp, otpId);
        });

        resendButton = view.findViewById(R.id.resendBtn);
        resendLabel = view.findViewById(R.id.labelCount);
        LinearLayout countdownContainer = view.findViewById(R.id.countdownContainer);

        resendButton.setOnClickListener(v -> {
            sendOTP();
            resendButton.setEnabled(false);
            countdownContainer.setVisibility(View.VISIBLE);

            new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    resendLabel.setText("Resend in " + millisUntilFinished / 1000 + " seconds");
                }

                public void onFinish() {
                    resendButton.setEnabled(true);
                    countdownContainer.setVisibility(View.GONE);
                }
            }.start();
        });

        // Check if OTP was already sent
        if (!isOtpSent) {
            sendOTP(); // Send OTP on view creation only if not already sent
        }

        return view;
    }

    private void verifyOTP(String otp, String otpID) {
        try {
            if (!isAdded()) {
                Log.w(TAG, "Fragment not attached. Skipping verifyOTP.");
                return;
            }

            Dialog.showLoading(getContext());

            PBAuth.getInstance().authWithOTP(otpID, otp, new PBCallback() {
                @Override
                public void onSuccess(String result) {
                    if (!isAdded()) return;

                    try {
                        requireActivity().runOnUiThread(() -> {

                            Dialog.exitLoading();
                            Toast.makeText(getContext(), "OTP Verified!", Toast.LENGTH_SHORT).show();
                            Router.getInstance(getParentFragmentManager())
                                    .switchFragment(new SuccessFragment(), false, R.id.auth_container);

                        });
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Fragment not attached when handling OTP success", e);
                    }
                }

                @Override
                public void onError(String error) {
                    if (!isAdded()) return;

                    try {
                        requireActivity().runOnUiThread(() -> {
                            sharedOtpWatcher.resetCurrentIndex();
                            for (EditText otpField : otpFields) {
                                if (otpField != null) otpField.setText("");
                            }

                            otpProcessed = false;
                            Dialog.exitLoading();
                            Dialog.showError(getContext(), error, null);
                        });
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Fragment not attached when handling OTP error", e);
                    }
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "Exception in verifyOTP: ", ex);
        }
    }

    private void sendOTP() {
        try {
            // If OTP was already sent, skip sending it again
            if (isOtpSent) {
                Log.d(TAG, "OTP already sent, skipping...");
                return;
            }

            PBAuth.getInstance().requestOTP(mParam1, new PBCallback() {
                @Override
                public void onSuccess(String response) {
                    if (!isAdded()) return;

                    try {
                        requireActivity().runOnUiThread(() -> {

                            Dialog.exitLoading();

                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                otpId = jsonResponse.getString("otpId");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            otpProcessed = false;
                            isOtpSent = true;  // Mark OTP as sent

                            Log.d(TAG, "OTP sent successfully, ready for new input.");
                        });
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Fragment not attached when handling OTP send success", e);
                    }
                }

                @Override
                public void onError(String error) {
                    if (!isAdded()) return;

                    try {
                        requireActivity().runOnUiThread(() -> {

                            Dialog.exitLoading();
                            otpProcessed = false;
                            Dialog.showError(getContext(), error, null);
                        });
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Fragment not attached when handling OTP send error", e);
                    }
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "Exception in sendOTP: ", ex);
        }
    }

    // Save the OTP sent state when the fragment is paused or the configuration changes
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isOtpSent", isOtpSent);
        outState.putString("otpID", otpId);
    }

}
