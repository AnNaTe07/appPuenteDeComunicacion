package com.softannate.apppuentedecomunicacion.base;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.Observer;
import com.softannate.apppuentedecomunicacion.modelos.Categoria_Mensaje;
import com.softannate.apppuentedecomunicacion.utils.CategoriaPopUpWindow;

import java.util.List;


public class FragmentNuevoConversacion<T extends ViewModelConversacionNuevo> extends FragmentBase<T>{
protected TextView tvCategoria;
    protected int idEtiqueta=0;
    protected EditText editMensaje;

    @Override
    protected void configurarObservers(T viewModel) {
        super.configurarObservers(viewModel);

        viewModel.getMensaje().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCategorias().observe(getViewLifecycleOwner(), new Observer<List<Categoria_Mensaje>>() {
            @Override
            public void onChanged(List<Categoria_Mensaje> msgEtiquetas) {
                cargarEtiquetas(tvCategoria,msgEtiquetas);
            }
        });
    }
    protected void limpiarCampos(){
        editMensaje.setText("");
        tvCategoria.setText("Categoría");
    }
    private void cargarEtiquetas(TextView tvCategoria,List<Categoria_Mensaje> categorias) {
        tvCategoria.setOnClickListener(v -> {
            CategoriaPopUpWindow categoriaPopUpWindow = new CategoriaPopUpWindow(
                    requireContext(),
                    categorias,
                    categoriaSeleccionada -> {
                        tvCategoria.setText(categoriaSeleccionada.getNombre());
                        idEtiqueta=categoriaSeleccionada.getId();
                    }
            );
            categoriaPopUpWindow.mostrar(tvCategoria);
        });
    }
}


