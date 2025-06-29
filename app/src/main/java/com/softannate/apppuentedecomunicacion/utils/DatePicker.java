package com.softannate.apppuentedecomunicacion.utils;

import android.widget.EditText;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.softannate.apppuentedecomunicacion.R;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DatePicker {

    private static Long fechaInicioMillis = null;
    private static Long fechaFinMillis = null;

    public static void showFechaInicioPicker(FragmentActivity activity, EditText etInicio) {
        long hoy = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Validar que no sea posterior a hoy ni a la fecha fin (si existe)
        CalendarConstraints.DateValidator hastaHoy = DateValidatorPointBackward.before(hoy);
        CalendarConstraints.DateValidator hastaFin = (fechaFinMillis != null)
                ? DateValidatorPointBackward.before(fechaFinMillis + 1)
                : hastaHoy;

        constraintsBuilder.setEnd(hoy);
        constraintsBuilder.setValidator(CompositeDateValidator.allOf(Arrays.asList(hastaHoy, hastaFin)));

        MaterialDatePicker<Long> pickerInicio = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccioná la fecha de inicio")
                .setTheme(R.style.CustomDatePickerTheme)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        pickerInicio.addOnPositiveButtonClickListener(selection -> {
            fechaInicioMillis = selection;
            etInicio.setText(formatearFecha(selection));
        });

        pickerInicio.show(activity.getSupportFragmentManager(), "PICKER_INICIO");
    }

    public static void showFechaFinPicker(FragmentActivity activity, EditText etFin) {
        long hoy = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Validar que no sea anterior a la fecha inicio ni posterior a hoy
        CalendarConstraints.DateValidator desdeInicio = (fechaInicioMillis != null)
                ? DateValidatorPointForward.from(fechaInicioMillis)
                : DateValidatorPointForward.from(0L);

        CalendarConstraints.DateValidator hastaHoy = DateValidatorPointBackward.before(hoy);

        constraintsBuilder.setEnd(hoy);
        if (fechaInicioMillis != null) {
            constraintsBuilder.setStart(fechaInicioMillis);
        }

        constraintsBuilder.setValidator(CompositeDateValidator.allOf(Arrays.asList(desdeInicio, hastaHoy)));

        MaterialDatePicker<Long> pickerFin = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccioná la fecha de fin")
                .setTheme(R.style.CustomDatePickerTheme)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        pickerFin.addOnPositiveButtonClickListener(selection -> {
            fechaFinMillis = selection;
            etFin.setText(formatearFecha(selection));
        });

        pickerFin.show(activity.getSupportFragmentManager(), "PICKER_FIN");
    }

    private static String formatearFecha(Long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //para corregir el desfase de fecha
        return sdf.format(new Date(millis));
    }

    public static void resetFechaInicio() {
        fechaInicioMillis = null;
    }
    public static void resetFechaFin() {
        fechaFinMillis = null;
    }
}