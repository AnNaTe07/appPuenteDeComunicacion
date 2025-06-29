package com.softannate.apppuentedecomunicacion.utils;

import android.app.Activity;
import android.os.Build;

import androidx.fragment.app.Fragment;

public class NavigationBarColor {
    public static void setNavigationBarColor(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(colorResId));
        }
    }
}
