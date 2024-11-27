package com.softannate.apppuentedecomunicacion.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.softannate.apppuentedecomunicacion.LoginActivity;
import com.softannate.apppuentedecomunicacion.MainActivity;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.modelos.Navegacion;
import com.softannate.apppuentedecomunicacion.ui.inicio.InicioFragment;

public class NavManager {

    public static void navegarAVista(Activity activity, Navegacion navegacion) {
        // Aquí solo manejas el Intent para cambiar entre actividades.
        switch (navegacion) {
            case INICIO:
                // Si el token es válido, se debe navegar al fragmento INICIO en MainActivity
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Nueva tarea para no volver a Splash
                intent.putExtra("navegar_a_inicio", true); // Indicamos que se debe navegar al fragmento INICIO
                activity.startActivity(intent);
                break;
            case LOGIN:
                intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                break;
        }
    }
}
