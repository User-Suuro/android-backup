package com.example.sariapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.helpers.Router;

import com.example.sariapp.helpers.db.pocketbase.PBConn;
import com.example.sariapp.ui.auth.FailedFragment;

import com.example.sariapp.ui.auth.SignupFragment;

import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    Router router;
    PBConn pb;

    // -- Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        String pb_url = "https://suuro.pockethost.io";
        String admin = "godwingalvez26@gmail.com";
        String password = "anatadare123";

        pb = PBConn.getInstance();

        if (!pb.isLoggedIn()) {
            authAdmin(pb, admin, password, pb_url);
        }

        router = Router.getInstance(getSupportFragmentManager(), R.id.auth_container);
    }

    private void authAdmin(PBConn pb, String email, String password, String pb_url) {
        AlertDialog.Builder loading = new AlertDialog.Builder(AuthActivity.this);
        AlertDialog.Builder error = new AlertDialog.Builder(AuthActivity.this);

        loading.setView(R.layout.dialog_loading);
        loading.setCancelable(false);  // Disable dismiss by back button

        error.setView(R.layout.dialog_error);
        error.setCancelable(true);

        AlertDialog progressDialog = loading.create();
        AlertDialog errorDialog = error.create();
        TextView errorTextView = errorDialog.findViewById(R.id.error_message);

        progressDialog.show();

        new Thread(() -> {
            pb.authenticateAdmin(email, password, pb_url, new PBConn.Callback() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String authToken = jsonResponse.getString("token");

                            // Save token using PrefsManager
                            pb.setToken(authToken);

                            Toast.makeText(AuthActivity.this, "Connection Established", Toast.LENGTH_LONG).show();
                            router.switchFragment(new SignupFragment(), false);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                            progressDialog.dismiss();
                            errorTextView.setText("Error: " + error);
                            router.switchFragment(new FailedFragment(), false);
                        }
                    );
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Router.clear();
    }
}

