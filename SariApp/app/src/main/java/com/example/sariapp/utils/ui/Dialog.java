package com.example.sariapp.utils.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.sariapp.R;

public class Dialog {

    // Show a non-cancelable loading dialog
    public static AlertDialog showLoading(Activity activity, View loadingView) {
        AlertDialog loadingDialog = new AlertDialog.Builder(activity)
                .setView(loadingView)
                .setCancelable(false)
                .create();
        loadingDialog.show();
        return loadingDialog;
    }

    // Dismiss the loading dialog
    public static void exitLoading(AlertDialog loadingDialog) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    // Show an error dialog with a message and a callback for the close button
    public static AlertDialog showError(Activity activity, View errorView, String errorMessage, Runnable onErrorBtn) {
        TextView errorTextView = errorView.findViewById(R.id.error_message);
        Button exitBtnError = errorView.findViewById(R.id.btn_close);

        errorTextView.setText(errorMessage);

        AlertDialog errorDialog = new AlertDialog.Builder(activity)
                .setView(errorView)
                .setCancelable(true)
                .create();

        exitBtnError.setOnClickListener(v -> {
            errorDialog.dismiss();
            if (onErrorBtn != null) {
                onErrorBtn.run();
            }
        });

        errorDialog.show();
        return errorDialog;
    }
}
