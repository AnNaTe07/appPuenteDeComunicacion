package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.tutor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar una lista expandible de destinatarios con un encabezado interactivo.
 * Permite seleccionar múltiples destinatarios mediante checkbox y sincroniza con el ViewModel.
 */
public class DestinatariosTutorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_HEADER = 0;
    private static final int TIPO_DESTINATARIO = 1;

    private final Context context;
    private final List<Object> items = new ArrayList<>();
    private final List<DestinatarioDto> destinatarios = new ArrayList<>();
    private boolean expandir = false;


    /**
     * Listener que se dispara cuando cambia la selección de destinatarios.
     * Ideal para sincronizar el estado desde el adapter hacia el ViewModel o la UI.
     */
    public interface OnSeleccionCambioListener {
        /**
         * Método llamado al cambiar la selección. Se envía la lista completa con sus estados actualizados.
         *
         * @param seleccionados Lista de destinatarios con su estado 'seleccionado'.
         */
        void onSeleccionCambio(List<DestinatarioDto> seleccionados);
    }

    private OnSeleccionCambioListener listener;

    /**
     * Registra el listener que recibe los cambios de selección.
     *
     * @param listener Implementación del callback.
     */
    public void setOnSeleccionCambioListener(OnSeleccionCambioListener listener) {
        this.listener = listener;
    }

    public DestinatariosTutorAdapter(Context context) {
        this.context = context;
    }

    /**
     * Recibe una lista de destinatarios y reconstruye el listado visual.
     *
     * @param lista Lista de destinatarios a mostrar (puede ser null o vacía).
     */
    public void setData(List<DestinatarioDto> lista) {
        destinatarios.clear();
        if (lista != null) destinatarios.addAll(lista);
        construirListaVisual();
    }

    /**
     * Crea la lista final de ítems que se muestran en el RecyclerView.
     * Incluye el header y opcionalmente los destinatarios según el estado 'expandir'.
     */
    private void construirListaVisual() {
        items.clear();
        items.add("📨 Destinatarios disponibles");
        if (expandir) items.addAll(destinatarios);
        notifyDataSetChanged();
    }


    /**
     * Determina el tipo de vista según el ítem (header o destinatario).
     */
    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        return (item instanceof String) ? TIPO_HEADER : TIPO_DESTINATARIO;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Crea el ViewHolder adecuado según el tipo (header o destinatario).
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int tipo) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (tipo == TIPO_HEADER) {
            View v = inflater.inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_persona, parent, false);
            return new PersonaViewHolder(v);
        }
    }

    /**
     * Vincula cada ítem con su ViewHolder, incluyendo lógica de selección y expansión.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        Object item = items.get(position);

        //  HEADER
        if (vh instanceof HeaderViewHolder) {
            HeaderViewHolder h = (HeaderViewHolder) vh;
            String texto = (String) item;
            h.tv.setText(texto);

            //  Tocar el título expande/colapsa
            h.tv.setOnClickListener(v -> {
                expandir = !expandir;
                construirListaVisual();
            });

            //  CheckBox: marcar todos los destinatarios
            h.cb.setOnCheckedChangeListener(null);

            boolean todosMarcados = !destinatarios.isEmpty()
                    && destinatarios.stream().allMatch(DestinatarioDto::isSeleccionado);
            h.cb.setChecked(todosMarcados);

            h.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                for (DestinatarioDto d : destinatarios) d.setSeleccionado(isChecked);
                notificarCambio();
                notifyDataSetChanged();
            });
        }

        // DESTINATARIO
        else if (vh instanceof PersonaViewHolder) {
            PersonaViewHolder h = (PersonaViewHolder) vh;
            DestinatarioDto d = (DestinatarioDto) item;

            h.nombre.setText(d.getNombre());
            h.cb.setOnCheckedChangeListener(null);
            h.cb.setChecked(d.isSeleccionado());

            h.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                d.setSeleccionado(isChecked);
                notificarCambio();
                Log.d("DESTINATARIO1", "ID: " + d.getId() + ", Nombre: " + d.getNombre() + ", Seleccionado: " + isChecked);

                //  Actualiza visualmente el header
                int headerIndex = items.indexOf("📨 Destinatarios disponibles");
                if (headerIndex != -1) notifyItemChanged(headerIndex);
            });
        }
    }
    /**
     * Notifica al listener externo el estado actual completo de todos los destinatarios.
     * Se usa tras cualquier interacción que modifique la selección.
     */
    private void notificarCambio() {
        if (listener != null) {
            listener.onSeleccionCambio(new ArrayList<>(destinatarios)); // paso toda la lista con estados reales

        }
    }

    /**
     * ViewHolder para el encabezado, que incluye título y checkbox para seleccionar todos.
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        CheckBox cb;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvHeader);
            cb = itemView.findViewById(R.id.cbHeader);
        }
    }

    /**
     * ViewHolder para cada destinatario, con nombre y checkbox individual.
     */
    static class PersonaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        CheckBox cb;

        PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombre);
            cb = itemView.findViewById(R.id.cbPersona);
        }
    }

    /**
     * Desmarca todos los destinatarios y actualiza la vista.
     * Útil al limpiar el formulario o reiniciar la selección.
     */
    public void resetear() {
        for (DestinatarioDto d : destinatarios) d.setSeleccionado(false);
        notifyDataSetChanged();
    }

    /**
     * Colapsa la lista visual ocultando los destinatarios, dejando solo el header.
     */
    public void contraerLista() {
        expandir = false;
        construirListaVisual(); // reconstruye el listado solo con el header
    }

}
