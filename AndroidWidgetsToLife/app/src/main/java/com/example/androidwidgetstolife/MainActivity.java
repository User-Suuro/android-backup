package com.example.androidwidgetstolife;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);

        // Create a new LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 50, 50, 50);

        scrollView.addView(linearLayout);
        setContentView(scrollView);

        // EDITTEXT

        Button myButton = new Button(this);
        myButton.setText("Create Text");
        EditText editText = new EditText(this);

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() == 0) { return; }
                TextView content = new TextView(getApplicationContext());
                content.setText(editText.getText());
                contentLayout.addView(content);
                editText.setText("");
            }
        });

        // IMAGE VIEW

        Button addImgBtn = new Button(this);
        addImgBtn.setText("Add Image");

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(500, 500)); // Set size
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // Initialize ActivityResultLauncher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();

                        // Load image into ImageView using Glide
                        Glide.with(this)
                                .load(imageUri)
                                .into(imageView);

                        addImgBtn.setText("Change Image");
                    }
                }
        );

        addImgBtn.setOnClickListener(v -> openGallery());

        // SWITCHES

        Switch themeSwitch = new Switch(this);
        themeSwitch.setText("Dark Mode");

        if (isDarkMode()) {
            themeSwitch.setChecked(true);
        }

        // Handle Switch Toggle
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        });

        // CHECKBOX

        ImageView vector = new ImageView(this);
        vector.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        vector.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground);
        vector.setImageDrawable(drawable);

        // Create CheckBoxes
        CheckBox transChk = new CheckBox(this);
        CheckBox tintChk = new CheckBox(this);
        CheckBox resizeChk = new CheckBox(this);

        transChk.setText("Transparency");
        tintChk.setText("Tint");
        resizeChk.setText("Resize");

        // TRANSPARENCY: Adjust Alpha
        transChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vector.setAlpha(isChecked ? 0.5f : 1.0f); // 50% transparent if checked
            }
        });

        // TINT: Apply Color Tint
        tintChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vector.setColorFilter(Color.RED); // Apply red tint
                } else {
                    vector.clearColorFilter(); // Remove tint
                }
            }
        });

        // RESIZE: Increase Image Size
        resizeChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ViewGroup.LayoutParams params = vector.getLayoutParams();
                params.width = isChecked ? 500 : 300; // Increase width if checked
                params.height = isChecked ? 500 : 300; // Increase height if checked
                vector.setLayoutParams(params);
            }
        });

        // TEXTCLOCK

        // Create TextClock
        TextClock textClock = new TextClock(this);
        textClock.setFormat12Hour("hh:mm:ss a"); // 12-hour format with AM/PM
        textClock.setFormat24Hour("HH:mm:ss");   // 24-hour format
        textClock.setTextSize(40);  // Set font size
        textClock.setPadding(20, 20, 20, 20);

        // RADIO GROUP AND RADIO BUTON

        // Create RadioGroup
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);


        // Create and add RadioButtons dynamically
        String[] options = {"London", "Beijing", "New York"};

        for (int i = 0; i < options.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(options[i]);
            radioButton.setId(View.generateViewId()); // Generate unique ID
            radioGroup.addView(radioButton);
        }

        // Create Submit Button
        Button submitButton = new Button(this);
        TextView selectedText = new TextView(this);
        selectedText.setText("-----");

        submitButton.setText("Submit");
        submitButton.setOnClickListener(v -> {
            // Get selected RadioButton
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                selectedText.setText("Selected: " + selectedRadioButton.getText());
                Toast.makeText(this, "Selected: " + selectedRadioButton.getText(), Toast.LENGTH_SHORT).show();

                if (selectedRadioButton.getText().equals("London")) {
                    textClock.setTimeZone("Europe/London");
                } else if (selectedRadioButton.getText().equals("Beijing")) {
                    textClock.setTimeZone("Etc/GMT-8");
                } else if (selectedRadioButton.getText().equals("New York")) {
                    textClock.setTimeZone("America/New_York");
                }

            } else {
                Toast.makeText(this, "No option selected", Toast.LENGTH_SHORT).show();
                selectedText.setText("-----");
            }
        });

        // WEBVIEW

        // Create WebView
        WebView webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1200, 400));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webSettings.setDomStorageEnabled(true); // Enable storage for web apps
        webSettings.setLoadWithOverviewMode(true); // Load page in overview mode
        webSettings.setUseWideViewPort(true); // Fit content to screen
        webSettings.setSupportZoom(true); // Enable zoom
        webSettings.setBuiltInZoomControls(true); // Enable pinch-to-zoom
        webSettings.setDisplayZoomControls(false); // Hide zoom buttons

        // Prevent opening in external browser
        webView.setWebViewClient(new WebViewClient());

        // Load URL
        webView.loadUrl("https://www.google.com");

        // Add Views to Layout

        linearLayout.addView(editText);
        linearLayout.addView(myButton);
        linearLayout.addView(contentLayout);

        linearLayout.addView(addImgBtn);
        linearLayout.addView(imageView);
        linearLayout.addView(themeSwitch);

        linearLayout.addView(vector);
        linearLayout.addView(transChk);
        linearLayout.addView(tintChk);
        linearLayout.addView(resizeChk);

        linearLayout.addView(textClock);
        linearLayout.addView(radioGroup);
        linearLayout.addView(submitButton);
        linearLayout.addView(selectedText);

        linearLayout.addView(webView);


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    public void onClick(View view) {

    }
}