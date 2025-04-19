package com.example.sariapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private Router router = new Router(getSupportFragmentManager());
    private PBAuth auth;
    private PBSession userSession;
    private PBSession adminSession;
    private final String admin = EnvConfig.PB_ADMIN_EMAIL;
    private final String password = EnvConfig.PB_ADMIN_PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();

        auth = PBAuth.getInstance();
        adminSession = PBSession.getAdminInstance(getApplicationContext());
        userSession = PBSession.getUserInstance(getApplicationContext());

        if (userSession.isLoggedIn()) {
            refreshSession(userSession);
        } else if (adminSession.isLoggedIn()) {
            refreshSession(adminSession);
        } else {
            authAdmin(auth, admin, password);
        }

        router.switchFragment(new LoginFragment(), false, R.id.auth_container);
    }

    private void refreshSession(PBSession session) {
        Dialog.showLoading(this);

        if (session == userSession) {
            new Thread(() -> {
                auth.refreshUserToken(session.getToken(), new PBCallback() {
                    @Override
                    public void onSuccess(String responseBody) {
                        runOnUiThread(() -> {
                            Dialog.exitLoading();
                            try {
                                JSONObject json = new JSONObject(responseBody);
                                String newToken = json.getString("token");
                                JSONObject record = json.getJSONObject("record");

                                session.setToken(newToken);
                                session.setRecord(record);

                                Toast.makeText(getApplicationContext(), "User session refreshed", Toast.LENGTH_SHORT).show();

                                // Redirect to main
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to parse user session", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Dialog.exitLoading();
                            Toast.makeText(getApplicationContext(), "User session refresh failed", Toast.LENGTH_SHORT).show();
                            userSession.clear();
                            // User will stay on login screen
                        });
                    }
                });
            }).start();
        } else if (session == adminSession) {
            new Thread(() -> {
                auth.refreshAdminToken(session.getToken(), new PBCallback() {
                    @Override
                    public void onSuccess(String responseBody) {
                        runOnUiThread(() -> {
                            Dialog.exitLoading();
                            try {
                                JSONObject json = new JSONObject(responseBody);
                                String newToken = json.getString("token");
                                JSONObject record = json.getJSONObject("record");

                                session.setToken(newToken);
                                session.setRecord(record);

                                Toast.makeText(getApplicationContext(), "Admin session refreshed", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to parse admin session", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Dialog.exitLoading();
                            Toast.makeText(getApplicationContext(), "Admin session refresh failed", Toast.LENGTH_SHORT).show();
                            adminSession.clear();
                            authAdmin(auth, admin, password);
                        });
                    }
                });
            }).start();
        }
    }

    private void authAdmin(PBAuth pb, String email, String password) {
        Dialog.showLoading(this);

        new Thread(() -> {
            pb.authenticateAdmin(email, password, new PBCallback() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();
                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String authToken = jsonResponse.getString("token");
                            JSONObject record = jsonResponse.getJSONObject("record");

                            PBSession session = PBSession.getAdminInstance(getApplicationContext());
                            session.setToken(authToken);
                            session.setRecord(record);

                            Toast.makeText(AuthActivity.this, "Admin connected", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Dialog.showError(AuthActivity.this, "Parse error: " + e.getMessage(), () -> {});
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();
                        Dialog.showError(AuthActivity.this, "Error: " + error, () -> finish());
                        router.switchFragment(new FailedFragment(), false, R.id.auth_container);
                    });
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
