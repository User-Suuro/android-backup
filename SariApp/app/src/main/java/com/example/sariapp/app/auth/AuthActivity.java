package com.example.sariapp.app.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.utils.Router;

import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.ui.Dialog;

import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    Router router;
    PBAuth pb;

    // -- Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        String admin = "godwingalvez26@gmail.com";
        String password = "anatadare123";

        pb = PBAuth.getInstance();

        if (!pb.isLoggedIn()) {
            authAdmin(pb, admin, password);
        }

        router = Router.getInstance(getSupportFragmentManager(), R.id.auth_container);
    }

    private void authAdmin(PBAuth pb, String email, String password) {
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the loading and error views once
        View loadingView = inflater.inflate(R.layout.dialog_loading, null);
        View errorView = inflater.inflate(R.layout.dialog_error, null);

        // Show loading dialog
        AlertDialog loadingDialog = Dialog.showLoading(AuthActivity.this, loadingView);

        new Thread(() -> {
            pb.authenticateAdmin(email, password, new PBAuth.Callback() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading(loadingDialog);  // Hide loading

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String authToken = jsonResponse.getString("token");

                            pb.setToken(authToken);
                            Toast.makeText(AuthActivity.this, "Connection Established", Toast.LENGTH_LONG).show();

                            // Switch to signup fragment
                            router.switchFragment(new SignupFragment(), false);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Dialog.showError(AuthActivity.this,  errorView, e.getMessage().toString(), () -> {});
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading(loadingDialog);  // Hide loading

                        // Show error dialog with callback
                        Dialog.showError(AuthActivity.this, errorView, "Error: " + error, () -> {
                            finish();  // Exit app or activity on close
                        });

                        router.switchFragment(new FailedFragment(), false);
                    });
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Router.clear();
    }

    public FrameLayout getAuthContainer () {
        return findViewById(R.id.auth_container);
    }
}

