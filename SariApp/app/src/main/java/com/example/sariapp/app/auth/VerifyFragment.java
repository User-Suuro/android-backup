package com.example.sariapp.app.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sariapp.R;
import com.example.sariapp.utils.Router;
import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.ui.Dialog;
import com.example.sariapp.utils.ui.Otp;
import com.google.android.material.textfield.TextInputLayout;

public class VerifyFragment extends Fragment {

    private static final String ARG_PARAM1 = "user_email";
    private static final String TAG = "VerifyFragment";
    private String mParam1;
    private String otpId;

    private AlertDialog loadingDialog;
    private View loadingView;

    private Button resendButton;
    private TextView resendLabel;
    private boolean otpProcessed = false;
    private boolean isOtpSent = false;  // New flag to track OTP sent state

    private EditText[] otpFields;
    private Otp sharedOtpWatcher;

    public VerifyFragment() {}

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
        } else {
            Toast.makeText(getContext(), "Email not provided.", Toast.LENGTH_SHORT).show();
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

        loadingView = inflater.inflate(R.layout.dialog_loading, null);

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
            loadingDialog = Dialog.showLoading(requireActivity(), loadingView);
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

            if (loadingDialog == null || !loadingDialog.isShowing()) {
                View loadingView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null);
                loadingDialog = Dialog.showLoading(requireActivity(), loadingView);
            }

            PBAuth.getInstance().authWithOTP(otpID, otp, new PBAuth.Callback() {
                @Override
                public void onSuccess(String result) {
                    if (!isAdded()) return;

                    try {
                        requireActivity().runOnUiThread(() -> {
                            if (loadingDialog != null) {
                                Dialog.exitLoading(loadingDialog);
                            }

                            Toast.makeText(getContext(), "OTP Verified!", Toast.LENGTH_SHORT).show();
                            if (requireActivity() instanceof AuthActivity) {
                                FrameLayout container = ((AuthActivity) requireActivity()).getAuthContainer();
                                Router.getInstance(getParentFragmentManager(), container.getId())
                                        .switchFragment(new SuccessFragment(), false);
                            } else {
                                Toast.makeText(getContext(), "Navigation error: Not AuthActivity", Toast.LENGTH_SHORT).show();
                            }
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

                            if (loadingDialog != null) {
                                Dialog.exitLoading(loadingDialog);
                            }

                            View freshErrorView = LayoutInflater.from(requireContext())
                                    .inflate(R.layout.dialog_error, null);

                            Dialog.showError(requireActivity(), freshErrorView,
                                    "Verification failed: " + error, null);
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

            PBAuth.getInstance().requestOTP(mParam1, new PBAuth.Callback() {
                @Override
                public void onSuccess(String resultOtpId) {
                    if (!isAdded()) return;

                    try {
                        requireActivity().runOnUiThread(() -> {
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                Dialog.exitLoading(loadingDialog);
                            }

                            otpId = resultOtpId;
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
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                Dialog.exitLoading(loadingDialog);
                            }

                            otpProcessed = false;

                            View freshErrorView = LayoutInflater.from(requireContext())
                                    .inflate(R.layout.dialog_error, null);

                            Dialog.showError(requireActivity(), freshErrorView,
                                    "OTP Failed to send: " + error,
                                    () -> {
                                        Toast.makeText(getContext(), "Retrying...", Toast.LENGTH_SHORT).show();
                                        sendOTP();
                                    });
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
