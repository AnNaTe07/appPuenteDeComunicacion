package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.educacion;


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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DestinatarioAdapter extends RecyclerView.Adapter<DestinatarioAdapter.ViewHolder> {

    private final List<DestinatarioDto> lista;
    private final Set<Integer> seleccionados = new HashSet<>();
    private final NuevoMensajeEducacionViewModel viewModel;

    public DestinatarioAdapter(List<DestinatarioDto> lista, NuevoMensajeEducacionViewModel viewModel) {
        this.lista = lista;
        this.viewModel = viewModel;
    }
    public Set<Integer> getSeleccionados() {
        return seleccionados;
    }
    public void actualizarLista(List<DestinatarioDto> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
        notifyDataSetChanged();
    }
    public void actualizarSeleccionados(Set<Integer> idsSeleccionados) {
        seleccionados.clear();
        seleccionados.addAll(idsSeleccionados);
        notifyDataSetChanged();
    }

    public void seleccionarTodos() {
        for (DestinatarioDto d : lista) {
            seleccionados.add(d.getId());
        }
        notifyDataSetChanged();
    }

    public void deseleccionarTodos() {
        seleccionados.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DestinatarioDto d = lista.get(position);
        Log.d("Adapter", "Asignando listener a checkbox para ID: " + d.getId());
        holder.tvNombre.setText(d.getNombre());

        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setChecked(seleccionados.contains(d.getId()));

        holder.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Adapter", "Checkbox " + (isChecked ? "checked" : "unchecked") + " for ID: " + d.getId());

            if (isChecked) {
                seleccionados.add(d.getId());
            } else {
                seleccionados.remove(d.getId());
            }
            viewModel.toggleSeleccion(d.getId());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cbPersona);
            tvNombre = itemView.findViewById(R.id.tvNombre);
        }
    }
}
