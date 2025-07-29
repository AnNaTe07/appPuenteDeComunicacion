package com.softannate.apppuentedecomunicacion.ui.main.agenda;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.FragmentBase;
import com.softannate.apppuentedecomunicacion.databinding.FragmentAgendaBinding;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Fragmento que muestra los cursos y alumnos disponibles. Permite selección de curso,
 * búsqueda por nombre de alumno y llamados telefónicos.
 */
public class AgendaFragment extends FragmentBase {

    private AgendaViewModel vmAgenda;
    private FragmentAgendaBinding binding;
    private AgendaAdapter adapter;
    private SearchView sv;

    /**
     * Crea una nueva instancia del fragmento.
     */
    public static AgendaFragment newInstance() {
        return new AgendaFragment();
    }

    /**
     * Inflado de la vista, inicialización de ViewModel, adapter, observers y configuración de la búsqueda.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Configuración inicial (ViewModel, binding, adapter)
        vmAgenda = new ViewModelProvider(this).get(AgendaViewModel.class);
        binding = FragmentAgendaBinding.inflate(inflater, container, false);

        configurarObservers(vmAgenda);

        configurarAdapter();
        configurarObservadores();
        configurarBusqueda();
        configurarGrupoClick();
        configurarResultados();

        //cargo los cursos
        vmAgenda.cargarCursos();
        return binding.getRoot();

    }
    private void configurarAdapter() {
        adapter = new AgendaAdapter(requireContext(), new ArrayList<>(), new HashMap<>());
        binding.listaCursosAlumnos.setAdapter(adapter);
        // evalúa el número telefónico al hacer clic sobre un alumno
        adapter.setOnAlumnoClickListener(vmAgenda::evaluarTelefono);
    }
    // evalúa el número telefónico al hacer clic sobre un alumno
    //    adapter.setOnAlumnoClickListener(alumno -> {
    //    vmAgenda.evaluarTelefono(alumno);    });

    private void configurarObservadores() {
        // Observa los nombres de cursos para mostrarlos en la lista principal
        vmAgenda.getNombresCursos().observe(getViewLifecycleOwner(), nombres -> {
            adapter.setCursos(nombres);
        });

        // Cuando se obtienen los alumnos, se cargan en el adapter
        vmAgenda.getCursoConAlumnos().observe(getViewLifecycleOwner(), dto -> {
            adapter.setAlumnos(dto.nombreCurso, dto.alumnos);
            int index = adapter.getGroupPosition(dto.nombreCurso);
            binding.listaCursosAlumnos.expandGroup(index);
        });

        // Si se puede llamar, se inicia la intención de marcado telefónico
        vmAgenda.getLlamar().observe(getViewLifecycleOwner(), shouldDial -> {
            AlumnoDto alumno = vmAgenda.getAlumnoActual();
            String telefono = alumno.getTelefonos().get(0);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono));
            startActivity(intent);
        });
    }

    private void configurarBusqueda() {
        sv = binding.searchInput;

        // Detecta texto ingresado en el campo de búsqueda
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //para cuando se da enter
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                vmAgenda.textoBusqueda(newText);
                return true;
            }
        });
        ImageView closeIcon = sv.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black));
        // Acciones al tocar el icono de cerrar del buscador
        closeIcon.setOnClickListener(v -> {
            binding.searchInput.setQuery("", false);
            vmAgenda.cargarCursos();
            adapter.setCursos(vmAgenda.getNombresCursos().getValue());
        });
    }

    private void configurarGrupoClick() {
        // Maneja el clic sobre el grupo para cargar alumnos del curso seleccionado
        binding.listaCursosAlumnos.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            vmAgenda.seleccionarCurso((String) adapter.getGroup(groupPosition));
            binding.listaCursosAlumnos.setAdapter((ExpandableListAdapter) null);
            binding.listaCursosAlumnos.postDelayed(() -> {
                binding.listaCursosAlumnos.setAdapter(adapter);
                binding.listaCursosAlumnos.expandGroup(groupPosition);
            }, 50);

            return true;
        });
    }

    private void configurarResultados() {
        // Muestra resultados de búsqueda en el adapter
        vmAgenda.getResultadoBusqueda().observe(getViewLifecycleOwner(), lista -> {
            List<String> cursos = Collections.singletonList("Resultados");
            HashMap<String, List<AlumnoDto>> mapa = new HashMap<>();
            mapa.put("Resultados", lista);

            adapter.setCursos(cursos);
            adapter.setAlumnos("Resultados", lista);
            binding.listaCursosAlumnos.expandGroup(0);
        });

    }


    /**
     * Configura elementos visuales luego de la creación de la vista.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sv.setIconified(false); // Que esté expandido

        //para cambiar el color a la lupa
        ImageView searchIcon =(ImageView)sv.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorLupa), PorterDuff.Mode.SRC_IN);

        //color del sv
        EditText searchEditText = (EditText) sv.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        //color clearIcon
        ImageView clearIcon = (ImageView) sv.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black));

    }

}