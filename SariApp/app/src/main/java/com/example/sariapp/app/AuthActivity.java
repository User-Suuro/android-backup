package com.example.sariapp.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.app.auth.FailedFragment;
import com.example.sariapp.app.auth.LoginFragment;
import com.example.sariapp.app.auth.SignupFragment;
import com.example.sariapp.utils.db.pocketbase.PBSession;
import com.example.sariapp.utils.ui.Router;

import com.example.sariapp.utils.db.pocketbase.PBAuth;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.ui.Dialog;

import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    Router router;
    PBAuth pb;
    PBSession session;

    // -- Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();

        // temporarily use admin token for users registration
        String admin = "godwingalvez26@gmail.com";
        String password = "anatadare123";

        pb = PBAuth.getInstance();
        session = PBSession.getInstance();

        if (!session.isLoggedIn()) {
            authAdmin(pb, admin, password);
        }

        router = Router.getInstance(getSupportFragmentManager());
        router.setContainerId(R.id.auth_container);
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

                            // establish session credentials
                            session.setToken(authToken);
                            session.setRecord(jsonResponse.getJSONObject("record"));

                            Toast.makeText(AuthActivity.this, "Connection Established" , Toast.LENGTH_LONG).show();

                            // Switch to signup fragment

                        } catch (Exception e) {
                            e.printStackTrace();
                            Dialog.showError(AuthActivity.this, e.getMessage().toString(), () -> {});
                        }

                        router.switchFragment(new LoginFragment(), false);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Dialog.exitLoading();  // Hide loading

                        // Show error dialog with callback
                        Dialog.showError(AuthActivity.this, "Error: " + error, () -> {
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

