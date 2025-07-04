package com.softannate.apppuentedecomunicacion.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.educacion.NuevoMensajeEducacionViewModel;
import com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.tutor.NuevoMensajeTutorViewModel;

import java.util.List;

public class DestinatariosHelper {

    public static void manejarSeleccionEducacion(NuevoMensajeEducacionViewModel viewModel, int checkedId, RadioGroup group) {
        RadioButton selected = group.findViewById(checkedId);
        if (selected != null && selected.getTag() != null) {
            String tag = selected.getTag().toString();
            switch (tag) {
                case "PERSONAL":
                    viewModel.obtenerDestinatarios();
                    break;
                case "ALUMNOS":
                   // viewModel.obtenerAlumnosRelacionados();
                    break;
            }
        }
    }

    public static void manejarSeleccionTutor(NuevoMensajeTutorViewModel viewModel, int checkedId, RadioGroup group) {
        RadioButton selected = group.findViewById(checkedId);
        if (selected != null && selected.getTag() != null) {
            String tag = selected.getTag().toString();
            switch (tag) {
                case "SEGUN HIJOS":
                    //viewModel.obtenerDestinatarios();
                    break;
            }
        }
    }
}
