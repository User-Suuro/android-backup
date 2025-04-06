package com.example.sariapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sariapp.R;
import com.example.sariapp.helpers.db.local.prefs.PBToken;
import com.example.sariapp.helpers.db.pocketbase.PBConn;

import org.json.JSONObject;


public class AuthActivity extends AppCompatActivity {
    // -- Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        String pb_url = getString(R.string.PB_URL);
        String admin = getString(R.string.PB_ADMIN_EMAIL);
        String password = getString(R.string.PB_PASSWORD);

        PBConn pb = PBConn.getInstance();
        authAdmin(pb, admin, password, pb_url);
    }

    private void authAdmin(PBConn pb, String email, String password, String pb_url) {
        new Thread(() -> {
            pb.authenticateAdmin(email, password, pb_url, new PBConn.Callback() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String authToken = jsonResponse.getString("token");

                            // Save token using PrefsManager
                            PBToken pbToken = new PBToken(getApplicationContext());
                            pbToken.saveAdminToken(authToken);
                            pbToken.savePBUrl(pb_url);

                            Toast.makeText(AuthActivity.this, "Connection Established", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AuthActivity.this, "Error: " + result, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(AuthActivity.this, error, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }).start();
    }

}

