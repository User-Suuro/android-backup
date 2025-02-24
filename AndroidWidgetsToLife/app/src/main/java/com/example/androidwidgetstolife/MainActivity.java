package com.example.androidwidgetstolife;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a new LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 50, 50, 50);

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

        // RADIO GROUP AND RADIO BUTON

        // Create RadioGroup
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // Create and add RadioButtons dynamically
        String[] options = {"Option 1", "Option 2", "Option 3"};
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
            } else {
                Toast.makeText(this, "No option selected", Toast.LENGTH_SHORT).show();
                selectedText.setText("-----");
            }
        });

        linearLayout.addView(editText);
        linearLayout.addView(myButton);
        linearLayout.addView(contentLayout);

        linearLayout.addView(addImgBtn);
        linearLayout.addView(imageView);

        // Add RadioGroup and Button to LinearLayout
        linearLayout.addView(radioGroup);
        linearLayout.addView(submitButton);
        linearLayout.addView(selectedText);

        setContentView(linearLayout);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onClick(View view) {

    }
}