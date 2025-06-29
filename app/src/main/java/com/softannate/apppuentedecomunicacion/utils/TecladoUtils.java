package com.softannate.apppuentedecomunicacion.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class TecladoUtils {

    public static void ocultarTeclado(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void ocultarTeclado(Fragment fragment) {
        if (fragment.getView() != null) {
            InputMethodManager imm = (InputMethodManager) fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    public static void configurarOcultadoDeTeclado(View vista, Activity actividad) {
        if (!(vista instanceof EditText)) {
            vista.setOnTouchListener((v, event) -> {
                ocultarTeclado(actividad);
                return false;
            });
        }

        if (vista instanceof ViewGroup) {
            int cantidad = ((ViewGroup) vista).getChildCount();
            for (int i = 0; i < cantidad; i++) {
                View hijo = ((ViewGroup) vista).getChildAt(i);
                configurarOcultadoDeTeclado(hijo, actividad);
            }
        }
    }
}

