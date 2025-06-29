package com.softannate.apppuentedecomunicacion.utils;


import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static Date parseUtcDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isBeforeNow(String dateStr) {
        Date date = parseUtcDate(dateStr);
        return date == null || date.before(new Date());
    }

    public static String formatearFecha(String fechaStr) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaStr);
            return formatoSalida.format(fecha);
        } catch (Exception e) {
            e.printStackTrace();
            return fechaStr; // Retorno la cadena original en caso de error
        }
    }
    public static Date parseFechaDesdeEditText(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return null;  // Si el campo está vacío, devolvemos null
        }

        return parseFechaDesdeTexto(editText.getText().toString());  // Convierte el texto a Date
    }
    //convierto texto de fecha a Date
    public static Date parseFechaDesdeTexto(String fechaTexto) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return formato.parse(fechaTexto);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

