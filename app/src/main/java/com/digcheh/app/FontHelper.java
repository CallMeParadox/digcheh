package com.digcheh.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.material.textfield.TextInputLayout;

public class FontHelper {

    private static Typeface regular;
    private static Typeface bold;
    private static Typeface medium;

    public static void init(Context context) {
        if (regular == null && context != null) {
            try {
                Context appContext = context.getApplicationContext();
                regular = ResourcesCompat.getFont(appContext, R.font.vazirmatn_regular);
                bold = ResourcesCompat.getFont(appContext, R.font.vazirmatn_bold);
                medium = ResourcesCompat.getFont(appContext, R.font.vazirmatn_medium);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Typeface getRegular() { return regular; }
    public static Typeface getBold() { return bold; }
    public static Typeface getMedium() { return medium; }

    public static void applyVazirmatn(View root, Context context) {
        if (root == null || context == null) return;
        init(context);
        applyRecursively(root);
    }

    private static void applyRecursively(View view) {
        if (view == null) return;

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            Typeface currentTf = tv.getTypeface();
            boolean isBold = (currentTf != null && currentTf.isBold());

            if (isBold && bold != null) {
                tv.setTypeface(bold);
            } else if (regular != null) {
                tv.setTypeface(regular);
            }
        }

        if (view instanceof TextInputLayout) {
            TextInputLayout til = (TextInputLayout) view;
            if (regular != null) {
                til.setTypeface(regular);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyRecursively(group.getChildAt(i));
            }
        }
    }
}
