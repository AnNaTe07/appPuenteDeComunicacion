package com.softannate.apppuentedecomunicacion.utils;

import android.content.Context;
import android.content.Intent;
import com.softannate.apppuentedecomunicacion.LoginActivity;
import com.softannate.apppuentedecomunicacion.MainActivity;
import com.softannate.apppuentedecomunicacion.modelos.Navegacion;

public class NavManager {

    public static void navegarAVista(Context context, Navegacion navegacion) { //metodo para navegar a una vista

        Intent intent;
        //navega segun el estado dado del viewmodel
        switch (navegacion) {
            case INICIO:
                intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case LOGIN:
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
        }
    }
}
