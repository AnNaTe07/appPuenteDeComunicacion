package com.softannate.apppuentedecomunicacion.utils;

import android.widget.EditText;

import androidx.appcompat.widget.SearchView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FiltroMensajes {

        // aplico filtrado en base a las fechas y al texto del SearchView
   /*     public static void aplicarFiltrado( MensajesViewModel vmViewModel, EditText fechaInicio, EditText fechaFin, SearchView sv) {
            // Obtengo las fechas seleccionadas
            Date fechaInicioSeleccionada = parseFecha(fechaInicio.getText().toString());
            Date fechaFinSeleccionada = parseFecha(fechaFin.getText().toString());
            String searchText = sv.getQuery().toString(); // Obtengo el texto del SearchView


            // Llamo a la función para realizar el filtrado
            if (fechaInicioSeleccionada != null || fechaFinSeleccionada != null || !searchText.isEmpty()) {
                vmViewModel.buscarMensajes(searchText, fechaInicioSeleccionada, fechaFinSeleccionada);
            }else{
                vmViewModel.actualizarMensajes();
            }
        }*/

        // limpio los campos de fecha y SearchView
        public static void limpiarFiltros(EditText fechaInicio, EditText fechaFin) {
            if (fechaInicio != null ) {
                fechaInicio.setText("");
            }
            if (fechaFin != null) {
                fechaFin.setText("");
            }

        }

        public static void limpiarFiltrosSv( SearchView sv) {

        // Limpia el SearchView
        sv.setQuery("", false);
        sv.clearFocus();         // Quito el foco del SearchView
    }

        // parseo una fecha en formato dd/MM/yyyy a un objeto Date
        public static Date parseFecha(String fechaStr) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return dateFormat.parse(fechaStr);
            } catch (ParseException e) {
                return null;
            }
        }
}
