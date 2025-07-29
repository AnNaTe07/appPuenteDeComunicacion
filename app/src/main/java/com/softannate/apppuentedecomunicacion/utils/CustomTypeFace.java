package com.softannate.apppuentedecomunicacion.utils;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypeFace extends TypefaceSpan {
    private final Typeface nuevaFuente;

    public CustomTypeFace(Typeface fuente) {
        super("");
        nuevaFuente = fuente;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setTypeface(nuevaFuente);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.setTypeface(nuevaFuente);
    }
}

