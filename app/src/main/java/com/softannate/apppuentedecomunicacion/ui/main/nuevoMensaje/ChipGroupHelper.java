package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.modelos.dto.ChipDto;
import java.util.List;

/**
 * Clase auxiliar para renderizar dinámicamente una lista de chips en un ChipGroup.
 * Permite configurar cada chip con su estilo y comportamiento de cierre personalizado.
 */
public class ChipGroupHelper {

    /**
     * Interfaz para manejar el evento de cierre de un chip.
     * Se implementa para definir la acción que se debe realizar cuando el usuario elimina un chip.
     */
    public interface OnChipCerrarListener {
        /**
         * Se llama cuando el usuario hace clic en el ícono de cierre de un chip.
         *
         * @param id   ID único del chip.
         * @param tipo Tipo del chip (puede representar categoría o rol).
         */
        void alCerrarChip(int id, String tipo);
    }

    /**
     * Renderiza una lista de chips personalizados en el ChipGroup especificado.
     *
     * @param context   Contexto necesario para acceder a recursos.
     * @param grupo     ChipGroup en el que se insertarán los chips.
     * @param lista     Lista de objetos ChipDto que contienen datos del chip (nombre, id, tipo).
     * @param listener  Listener que se ejecuta cuando se cierra un chip.
     */
    public static void renderChips(Context context, ChipGroup grupo, List<ChipDto> lista, OnChipCerrarListener listener) {
        grupo.removeAllViews();//limpio el grupo antes de agregar chips

        for (ChipDto chipDto : lista) {
            Chip chip = new Chip(grupo.getContext());
            chip.setText(chipDto.getNombre());
            chip.setTextColor(ContextCompat.getColor(context, R.color.colorBase));
            chip.setChipBackgroundColorResource(R.color.colorGris);
            chip.setCloseIconVisible(true);
            chip.setCloseIconTint(ColorStateList.valueOf(context.getResources().getColor(R.color.colorBase)));

            // Listener para ícono de cierre
            chip.setOnCloseIconClickListener(v -> listener.alCerrarChip(chipDto.getId(), chipDto.getTipo()));

            grupo.addView(chip);
        }
    }
}
