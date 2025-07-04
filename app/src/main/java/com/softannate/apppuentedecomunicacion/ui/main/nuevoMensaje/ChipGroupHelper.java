package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;

import java.util.List;

public class ChipGroupHelper {
    public interface OnChipCerrarListener {
        void alCerrarChip(int id);
    }
    public static void renderChips(ChipGroup grupo, List<DestinatarioDto> lista, OnChipCerrarListener listener) {
        grupo.removeAllViews();
        for (DestinatarioDto d : lista) {
            Chip chip = new Chip(grupo.getContext());
            chip.setText(d.getNombre());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> listener.alCerrarChip(d.getId()));
            grupo.addView(chip);
        }
    }
}
