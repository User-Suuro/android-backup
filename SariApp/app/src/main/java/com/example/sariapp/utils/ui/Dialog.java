package com.example.sariapp.utils.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.sariapp.R;

public class Dialog {

    // Static reference to the loading dialog so it can be dismissed later
    private static AlertDialog loadingDialog;

    // Show a non-cancelable loading dialog using a layout resource
    public static AlertDialog showLoading(Context context) {
        View loadingView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);

        loadingDialog = new AlertDialog.Builder(context)
                .setView(loadingView)
                .setCancelable(false)
                .create();

        loadingDialog.show();
        return loadingDialog;
    }

    // Dismiss the loading dialog
    public static void exitLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    // Show an error dialog with a layout resource, message, and callback
    public static AlertDialog showError(Context context, String errorMessage, Runnable onErrorBtn) {
        View errorView = LayoutInflater.from(context).inflate(R.layout.dialog_error, null);
        TextView errorTextView = errorView.findViewById(R.id.error_message);
        Button exitBtnError = errorView.findViewById(R.id.btn_close);

        errorTextView.setText(errorMessage);

        AlertDialog errorDialog = new AlertDialog.Builder(context)
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
