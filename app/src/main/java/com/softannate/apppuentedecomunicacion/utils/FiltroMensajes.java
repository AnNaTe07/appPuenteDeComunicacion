package com.softannate.apppuentedecomunicacion.utils;

import android.widget.EditText;

import androidx.appcompat.widget.SearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FiltroMensajes {

        // limpio los campos de fecha y SearchView
        public static void limpiarFiltros(EditText fechaInicio, EditText fechaFin) {
            if (fechaInicio != null ) {
                fechaInicio.setText("");
            }
            if (fechaFin != null) {
                fechaFin.setText("");
            }

        }
}
