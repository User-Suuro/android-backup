package com.example.sariapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Assets {
    private static final String TAG = "AssetHelper";

    /**
     * Loads a text file from the assets folder and returns its content as a String.
     *
     * @param fileName Name of the asset file (e.g., "example.txt").
     * @param context  The context to access the AssetManager.
     * @return The content of the file, or an empty string if an error occurs.
     */
    public static String loadAssetTextFile(String fileName, Context context) {
        AssetManager assetManager = context.getAssets();
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = assetManager.open(fileName)) {
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            stringBuilder.append(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e(TAG, "Error loading asset text file: " + fileName, e);
        }
        return stringBuilder.toString();
    }

    /**
     * Loads an image from the assets folder into the specified ImageView using Glide.
     *
     * @param context   The context to use.
     * @param assetPath Relative path of the image within the assets folder (e.g., "images/sample.png").
     * @param imageView The target ImageView.
     */
    public static void loadImageFromAssets(Context context, String assetPath, ImageView imageView) {
        String fullAssetPath = "file:///android_asset/" + assetPath;
        Glide.with(context)
                .load(fullAssetPath)
                .into(imageView);
    }
}
