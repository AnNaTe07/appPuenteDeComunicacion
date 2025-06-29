package com.softannate.apppuentedecomunicacion.utils;


import android.util.Log;
import android.widget.EditText;
import com.softannate.apppuentedecomunicacion.ui.main.mensaje.listaChats.MensajesViewModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//para mostrar fecha en perfil, mensajes y enviar a la api
public class Fecha {



    // Método para formatear la fecha desde el formato dd/MM/yyyy a yyyy-MM-dd'T'HH:mm:ss
    public static String formatearFechaParaApi(String fechaStr) {
        try {
            //si la fecha no está vacía
            if (fechaStr == null || fechaStr.isEmpty()) {
                Log.e("FechaInput", "Fecha de entrada vacía o inválida");
                return null; // No continua si la fecha es vacía
            }

            // Parseo de la fecha en el formato dd/MM/yyyy
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaStr);

            // si el parseo fue exitoso
            if (fecha == null) {
                Log.e("FechaInput", "Error al parsear la fecha: " + fechaStr);
                return null; // Si no se pudo parsear, devolver null
            }

            // formateo la fecha al formato que la API espera (yyyy-MM-dd'T'HH:mm:ss)
            SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String fechaFormateada = formatoSalida.format(fecha);

            //Log.d("FechaInput(FECHA)", "Fecha formateada para la API: " + fechaFormateada);
            return fechaFormateada;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("FechaInput", "Error al formatear la fecha: " + fechaStr);
            return null; // Si hubo un error al parsear, devuelvo null
        }
    }


        public static String formatearFechaMensaje(String fechaStr) {
            try {
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss 'hs'", Locale.getDefault());
                Date fecha = formatoEntrada.parse(fechaStr);
                return formatoSalida.format(fecha);
            } catch (Exception e) {
                e.printStackTrace();
                return fechaStr;
            }
        }
/*
    // obtengo las fechas desde los EditText y realizo la búsqueda
    public static void buscarConFechas(String newText, EditText fechaInicio, EditText fechaFin, MensajesViewModel vmViewModel) {
        // Obtengo las fechas desde los EditText
        Date fechaInicioDate = Fecha.parseFechaDesdeEditText(fechaInicio);
        Date fechaFinDate = Fecha.parseFechaDesdeEditText(fechaFin);

        // busco si hay texto o fechas
        if (!newText.isEmpty() || fechaInicioDate != null || fechaFinDate != null) {
            vmViewModel.buscarMensajes(newText, fechaInicioDate, fechaFinDate);
        }else{
          //  vmViewModel.actualizarMensajes();
        }
    }

 */
    //convierto el texto de un EditText a Date
    /*
    public static Date parseFechaDesdeEditText(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return null;  // Si el campo está vacío, devolvemos null
        }

        return parseFechaDesdeTexto(editText.getText().toString());  // Convierte el texto a Date
    }
    */
/*

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


 */
    /*
    public static String obtenerHora(String fechaStr) {
        try {
            // Definir el formato de entrada
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            // Definir el formato de salida para la hora
            SimpleDateFormat formatoSalida = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // Parsear la fecha de entrada
            Date fecha = formatoEntrada.parse(fechaStr);

            // Retornar solo la hora en formato HH:mm
            return formatoSalida.format(fecha);
        } catch (Exception e) {
            e.printStackTrace();
            return fechaStr; // Si ocurre un error, retorno la cadena original
        }
    }

     */
}

