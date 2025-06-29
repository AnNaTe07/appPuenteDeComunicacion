package com.softannate.apppuentedecomunicacion.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import java.util.Locale;

public class ResaltadorHelper {

    public static SpannableString resaltarCoincidencias(String texto, String termino, int color) {
        if (texto == null || termino == null || termino.isEmpty()) {
            return new SpannableString(texto);
        }

        SpannableString spannable = new SpannableString(texto);
        String textoLower = texto.toLowerCase();
        String terminoLower = termino.toLowerCase();

        int index = textoLower.indexOf(terminoLower);
        while (index >= 0) {
            spannable.setSpan(
                    new ForegroundColorSpan(color),
                    index,
                    index + termino.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            index = textoLower.indexOf(terminoLower, index + termino.length());
        }

        return spannable;
    }
    public static SpannableString resaltarFondoCoincidencias(String texto, String termino, int colorFondo) {
        if (texto == null) texto = "";
        if (termino == null || termino.isEmpty()) return new SpannableString(texto);

        SpannableString spannable = new SpannableString(texto);
        String textoLower = texto.toLowerCase(Locale.ROOT);
        String terminoLower = termino.toLowerCase(Locale.ROOT);

        int index = textoLower.indexOf(terminoLower);
        while (index >= 0) {
            spannable.setSpan(
                    new BackgroundColorSpan(colorFondo),
                    index,
                    index + termino.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            index = textoLower.indexOf(terminoLower, index + termino.length());
        }

        return spannable;
    }
}

