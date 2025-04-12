package com.example.sariapp.utils.ui;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class Otp implements TextWatcher, View.OnKeyListener, View.OnFocusChangeListener {

    private final EditText[] editTexts;
    private final OnOtpCompleteListener listener;
    private int currentIndex = -1;
    private boolean suppressChange = false;
    private boolean callbackTriggered = false;

    public Otp(EditText[] editTexts, OnOtpCompleteListener listener) {
        this.editTexts = editTexts;
        this.listener = listener;

        for (EditText et : editTexts) {
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            et.setOnKeyListener(this);
            et.setOnFocusChangeListener(this);
            et.addTextChangedListener(this);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            currentIndex = getIndex((EditText) v);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (suppressChange || currentIndex == -1) return;

        String input = editTexts[currentIndex].getText().toString();

        // Trim input to 1 char if needed
        if (input.length() > 1) {
            input = input.substring(0, 1);
            suppressChange = true;
            editTexts[currentIndex].setText(input);
            editTexts[currentIndex].setSelection(1); // Keep cursor in place
            suppressChange = false;
        }

        // Auto-move to next field
        if (!input.isEmpty() && currentIndex < editTexts.length - 1) {
            editTexts[currentIndex + 1].requestFocus();
        }

        // Trigger callback only once when last box is filled
        if (!callbackTriggered && currentIndex == editTexts.length - 1 && !input.isEmpty()) {
            StringBuilder otp = new StringBuilder();
            for (EditText et : editTexts) {
                String text = et.getText().toString();
                if (text.isEmpty()) return; // Incomplete
                otp.append(text);
            }

            callbackTriggered = true;
            if (listener != null) {
                listener.onOtpComplete(otp.toString());
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_DEL &&
                v instanceof EditText) {

            int index = getIndex((EditText) v);
            if (index > 0) {
                EditText current = editTexts[index];
                if (current.getText().toString().isEmpty()) {
                    editTexts[index - 1].setText("");
                    editTexts[index - 1].requestFocus();
                } else {
                    current.setText("");
                }
            }
        }
        return false;
    }

    private int getIndex(EditText et) {
        for (int i = 0; i < editTexts.length; i++) {
            if (editTexts[i] == et) return i;
        }
        return -1;
    }

    public void resetCurrentIndex() {
        currentIndex = -1;
        callbackTriggered = false;
        if (editTexts.length > 0) {
            editTexts[0].requestFocus();
        }
    }

    public interface OnOtpCompleteListener {
        void onOtpComplete(String otp);
    }
}
