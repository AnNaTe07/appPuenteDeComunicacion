package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.educacion;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.FragmentNuevoConversacion;
import com.softannate.apppuentedecomunicacion.databinding.FragmentNuevoMensajeEducacionBinding;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.ChipGroupHelper;
import com.softannate.apppuentedecomunicacion.utils.DestinatariosHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NuevoMensajeEducacionFragment extends FragmentNuevoConversacion {

    private NuevoMensajeEducacionViewModel vmNuevoE;
    private RadioGroup rg;
    private FragmentNuevoMensajeEducacionBinding binding;
    private DestinatarioAdapter adapter;
    private RecyclerView rvNuevoMensaje;

    public static NuevoMensajeEducacionFragment newInstance() {
        return new NuevoMensajeEducacionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNuevoMensajeEducacionBinding.inflate(inflater, container, false);

        vmNuevoE = new ViewModelProvider(this).get(NuevoMensajeEducacionViewModel.class);
        rg= binding.layoutRadios.rgDestinatarios;
        setRadioButtons();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                DestinatariosHelper.manejarSeleccionEducacion(vmNuevoE,checkedId,rg);
            }
        });
        adapter = new DestinatarioAdapter(new ArrayList<>(), vmNuevoE);

        binding.layoutDestinatarios.rvNuevoMensaje.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.layoutDestinatarios.rvNuevoMensaje.setAdapter(adapter);

        // Cuando llegan los destinatarios:
        vmNuevoE.getDestinatarios().observe(getViewLifecycleOwner(), lista -> {
            adapter.actualizarLista(lista);
        });
        //cambios en los IDs seleccionados.
        vmNuevoE.getIdsSeleccionados().observe(getViewLifecycleOwner(), ids -> {
            // Actualos los destinatarios seleccionados en el Adapter.
            adapter.actualizarSeleccionados(ids);
        });
        binding.layoutDestinatarios.checkBoxSeleccionarTodos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Cambia el estado global de selección en el ViewModel
            vmNuevoE.marcarSeleccionGlobal(isChecked);
        });


        vmNuevoE.getMostrarSeleccionarTodos().observe(getViewLifecycleOwner(), visible -> {
            binding.layoutDestinatarios.checkBoxSeleccionarTodos.setVisibility(visible ? View.VISIBLE : View.GONE);
        });
        vmNuevoE.getChipsVisibles().observe(getViewLifecycleOwner(), lista -> {
            Log.d("Chips", "Lista de chips visibles: " + lista.size());
            if (lista.isEmpty()) {
                Log.d("Chips", "No hay destinatarios seleccionados para mostrar.");
            } else {
                Log.d("Chips", "Destinatarios seleccionados para mostrar: " + lista.size());
            }
            ChipGroupHelper.renderChips(
                    binding.layoutChipGroup.cgDestinatarios,
                    lista,
                    id -> vmNuevoE.toggleSeleccion(id)
            );
        });


       // para acceder a los IDs elegidos:
        List<Integer> ids = new ArrayList<>(adapter.getSeleccionados());

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vmNuevoE = new ViewModelProvider(this).get(NuevoMensajeEducacionViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setRadioButtons() {
        RadioButton rbAlumnos = new RadioButton(requireContext());
        rbAlumnos.setId(View.generateViewId());
        rbAlumnos.setText("Alumnos");
        rbAlumnos.setTag("ALUMNOS");

        RadioButton rbPersonal = new RadioButton(requireContext());
        rbPersonal.setId(View.generateViewId());
        rbPersonal.setText("Personal educativo");
        rbPersonal.setTag("PERSONAL");

        rbAlumnos.setTextSize(16);
        rbPersonal.setTextSize(16);
        rbAlumnos.setButtonTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorBase));
        rbPersonal.setButtonTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorBase));

        rbAlumnos.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        rbPersonal.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        Typeface fuentePersonalizada = ResourcesCompat.getFont(requireContext(), R.font.adamina);
        rbAlumnos.setTypeface(fuentePersonalizada);
        rbPersonal.setTypeface(fuentePersonalizada);

        rg.addView(rbAlumnos);
        rg.addView(rbPersonal);
        rg.requestLayout();
        Log.d("RADIOGROUP", "Hijos: " + rg.getChildCount());

    }

}