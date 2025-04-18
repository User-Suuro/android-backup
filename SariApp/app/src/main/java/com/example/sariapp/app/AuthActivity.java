package com.example.sariapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.app.auth.FailedFragment;
import com.example.sariapp.app.auth.LoginFragment;
import com.example.sariapp.utils.EnvConfig;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.ui.Router;

import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.ui.Dialog;

import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    private Router router;
    private PBAuth auth;
    private PBSession userSession;
    private PBSession adminSession;
    private final String admin = EnvConfig.PB_ADMIN_EMAIL;
    private final String password = EnvConfig.PB_ADMIN_PASSWORD;

    // -- Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();

        auth = PBAuth.getInstance();
        adminSession = PBSession.getAdminInstance(getApplicationContext());
        router = Router.getInstance(getSupportFragmentManager());

        // Only refresh if logged in
        if (adminSession.isLoggedIn()) {
            refreshSession(adminSession.getToken());
        } else {
            authAdmin(auth, admin, password);
        }

        router.switchFragment(new LoginFragment(), false, R.id.auth_container);
    }

    private void refreshSession(String currentToken) {
        Dialog.showLoading(this);

        new Thread(() -> {
            auth.refreshAdminToken(currentToken, new PBCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();
                        try {
                            JSONObject json = new JSONObject(responseBody);
                            String newToken = json.getString("token");
                            JSONObject record = json.getJSONObject("record");

                            // Save updated token and record
                            adminSession.setToken(newToken);
                            adminSession.setRecord(record);

                            Toast.makeText(getApplicationContext(), "Session refreshed", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to parse refresh response", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        adminSession.clear();

                        // if failed to refresh authenticate then
                        authAdmin(auth, admin, password);
                    });
                }
            });
        }).start();
    }

    private void authAdmin(PBAuth pb, String email, String password) {
        Dialog.showLoading(this);

        new Thread(() -> {
            pb.authenticateAdmin(email, password, new PBCallback() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();  // Hide loading

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String authToken = jsonResponse.getString("token");
                            JSONObject record = jsonResponse.getJSONObject("record");

                            // Get session with context
                            PBSession session = PBSession.getAdminInstance(getApplicationContext());

                            // Store token and user record persistently
                            session.setToken(authToken);
                            session.setRecord(record);

                            Toast.makeText(AuthActivity.this, "Connection Established", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Dialog.showError(AuthActivity.this, "Parse error: " + e.getMessage(), () -> {});
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();  // Hide loading
                        Dialog.showError(AuthActivity.this, "Error: " + error, () -> {
                            finish();  // Optional: Exit activity
                        });

                        router.switchFragment(new FailedFragment(), false, R.id.auth_container);
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

