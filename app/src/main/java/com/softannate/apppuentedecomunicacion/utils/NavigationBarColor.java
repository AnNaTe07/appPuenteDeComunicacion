package com.softannate.apppuentedecomunicacion.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;

/**
 * Utilidad para configurar dinámicamente el color de la barra de navegación
 * según la orientación actual del dispositivo.
 * <p>
 * Aplica colores diferentes en modo retrato y paisaje, siempre que el dispositivo
 * esté corriendo Android 9 (Pie) o superior.
 */
public class NavigationBarColor {

    /**
     * Establece el color de la barra de navegación dependiendo de la orientación.
     *
     * @param activity             actividad desde la cual se modifica la UI
     * @param colorResIdPortrait   recurso de color para modo retrato
     * @param colorResIdLandscape  recurso de color para modo paisaje
     */
    public static void setNavigationBarColor(Activity activity, int colorResIdPortrait, int colorResIdLandscape) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            int orientation = activity.getResources().getConfiguration().orientation;

            int selectedColor = (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    ? activity.getResources().getColor(colorResIdLandscape)
                    : activity.getResources().getColor(colorResIdPortrait);

            activity.getWindow().setNavigationBarColor(selectedColor);
        }
    }
}

