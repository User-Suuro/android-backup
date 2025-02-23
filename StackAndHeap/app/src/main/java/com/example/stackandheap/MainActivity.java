package com.example.stackandheap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtView;
    Button prevBtn, nxtBtn, shrinkBtn, growBtn, hideBtn, resetBtn;
    private static int currIndex = 0;
    private static float txtSize = 0;
    private static float initTxtSize = 0;
    String[] alphabet = new String[26];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 26; i++) {
            alphabet[i] = String.valueOf((char) ('A' + i));
        }
        txtView = findViewById(R.id.textView);
        prevBtn = findViewById(R.id.prevBtn);
        nxtBtn = findViewById(R.id.nxtBtn);
        shrinkBtn = findViewById(R.id.shrinkBtn);
        growBtn = findViewById(R.id.growBtn);
        hideBtn = findViewById(R.id.hideBtn);
        resetBtn = findViewById(R.id.resetBtn);

        prevBtn.setOnClickListener(this);
        nxtBtn.setOnClickListener(this);
        shrinkBtn.setOnClickListener(this);
        growBtn.setOnClickListener(this);
        hideBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);

        txtSize = txtView.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        initTxtSize = txtSize;
    }

    public void onClick(View view){
        int viewID = view.getId();

        if (viewID == R.id.prevBtn) {
            currIndex--;
            if (currIndex < 0) {
                currIndex = alphabet.length - 1;
            }
            txtView.setText(alphabet[currIndex]);
        }

        else if (viewID == R.id.nxtBtn) {
             currIndex++;
             if (currIndex > alphabet.length - 1) {
                 currIndex = 0;
             }
             txtView.setText(alphabet[currIndex]);
        }

        else if (viewID == R.id.shrinkBtn) {
            if (txtSize != 0) {
              txtSize--;
            }
            txtView.setTextSize(txtSize);
        }

        else if (viewID == R.id.growBtn) {
           txtSize++;
           txtView.setTextSize(txtSize);
        }

        else if (viewID == R.id.hideBtn) {
           if (txtView.getVisibility() == View.VISIBLE) {
              txtView.setVisibility(View.INVISIBLE);
              hideBtn.setText("SHOW");
           }  else {
              txtView.setVisibility(View.VISIBLE);
              hideBtn.setText("HIDE");
           }
        }

        else if (viewID == R.id.resetBtn) {

            txtSize = initTxtSize;
            currIndex = 0;

            txtView.setVisibility(View.VISIBLE);
            txtView.setTextSize(txtSize);
            txtView.setText(alphabet[currIndex]);
        }
    }
}