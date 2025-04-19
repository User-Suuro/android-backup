package com.example.sariapp.utils.misc;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class FadePageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setTranslationX(-position * page.getWidth());

        if (position <= -1.0f || position >= 1.0f) {
            page.setAlpha(0.0f); // Page is not visible
        } else if (position == 0.0f) {
            page.setAlpha(1.0f); // Page is selected
        } else {
            // Fade the page relative to its distance from the center
            page.setAlpha(1.0f - Math.abs(position));
        }
    }
}
