package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.tutor;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.FragmentNuevoConversacion;
import com.softannate.apppuentedecomunicacion.databinding.FragmentNuevoMensajeTutorBinding;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeTutorDto;
import com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.ChipGroupHelper;


public class NuevoMensajeTutorFragment extends FragmentNuevoConversacion {

    private NuevoMensajeTutorViewModel vmNuevoTutor;
    private FragmentNuevoMensajeTutorBinding binding;
    private DestinatariosTutorAdapter adapter;
    private RecyclerView rvNuevoMensaje;
    private Integer idAlumno;

    /**
     * Instancia un nuevo fragmento de conversación con tutor.
     * @return Fragmento inicializado.
     */
    public static NuevoMensajeTutorFragment newInstance() {
        return new NuevoMensajeTutorFragment();
    }

    /**
     * Infla la vista del fragmento y configura todos los componentes visuales,
     * observers de ViewModel y listeners de UI.
     * @param inflater Inflador de vistas.
     * @param container Contenedor padre.
     * @param savedInstanceState Estado previamente guardado.
     * @return Vista principal del fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNuevoMensajeTutorBinding.inflate(inflater, container, false);
        vmNuevoTutor = new ViewModelProvider(this).get(NuevoMensajeTutorViewModel.class);

        configurarObservers(vmNuevoTutor);
        inicializarComponentesUI();
        configurarAutoComplete();
        configurarRecyclerView();
        observarViewModel();
        configurarListeners();
        this.idAlumno = vmNuevoTutor.getId();

        return binding.getRoot();
    }

    /**
     * Inicializa componentes de la UI como RecyclerView y su adapter.
     * Este método configura los elementos que no dependen de observables o listeners.
     */
    private void inicializarComponentesUI() {
        rvNuevoMensaje = binding.layoutDestinatarios.rvNuevoMensaje;
        adapter = new DestinatariosTutorAdapter(requireContext());
    }

    /**
     * Configura el AutoCompleteTextView con una lista de nombres de alumnos.
     * Define comportamiento visual del campo y su listener para selección de ítems.
     */
    private void configurarAutoComplete() {
        AutoCompleteTextView autoComplete = binding.autoCompleteAlumno;

        // Observa la lista de nombres disponibles y configura el adapter
        vmNuevoTutor.getNombresAlumnos().observe(getViewLifecycleOwner(), nombres -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.item_autocomplete_nombre, nombres);
            autoComplete.setAdapter(adapter);
            autoComplete.setFocusable(false);
            autoComplete.setFocusableInTouchMode(false);
            autoComplete.setClickable(true);
            autoComplete.setDropDownBackgroundResource(R.color.white);
        });

        // Listener para detección de alumno seleccionado
        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String nombreSeleccionado = parent.getItemAtPosition(position).toString();
            idAlumno = vmNuevoTutor.getIdPorNombre(nombreSeleccionado);
            Log.d("NuevoMensajeTutor", "Alumno seleccionado: " + idAlumno);
            vmNuevoTutor.cargarUsuariosPorAlumno(idAlumno);
            vmNuevoTutor.limpiarDestinatarios();
        });
    }

    /**
     * Configura el RecyclerView de destinatarios con su LayoutManager y adapter.
     */
    private void configurarRecyclerView() {
        rvNuevoMensaje.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNuevoMensaje.setAdapter(adapter);
    }

    /**
     * Establece todos los observers sobre el ViewModel. Permite mantener la UI actualizada
     * frente a cambios en los datos, como lista de destinatarios, visibilidad de vistas, chips, etc.
     */
    private void observarViewModel() {
        vmNuevoTutor.getLimpiar().observe(getViewLifecycleOwner(), aBoolean -> {
            limpiarCampos();
            binding.layoutChipGroup.cgDestinatarios.removeAllViews();
            adapter.resetear();
            binding.autoCompleteAlumno.setText("");
        });

        //actualiza la lista de destinatarios
        vmNuevoTutor.getDestinatarios().observe(getViewLifecycleOwner(), usuarios -> adapter.setData(usuarios));

        //muestro autocomplete de hijos si hay mas de 1
        vmNuevoTutor.getMostrarVista().observe(getViewLifecycleOwner(),aBoolean-> {
            binding.layoutHijos.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
        });

        vmNuevoTutor.getMostrarDestinatarios().observe(getViewLifecycleOwner(), aBoolean -> {
            vmNuevoTutor.cargarUsuariosPorAlumno(vmNuevoTutor.getId());
            rvNuevoMensaje.setAdapter(adapter);
        });

        vmNuevoTutor.getChips().observe(getViewLifecycleOwner(), chips -> {
            ChipGroupHelper.renderChips(
                    requireContext(),
                    binding.layoutChipGroup.cgDestinatarios,
                    chips,
                    (id, tipo) -> vmNuevoTutor.cerrarChip(id, tipo) // aviso al ViewModel
            );
        });
    }

    /**
     * Configura los listeners necesarios para el adapter, como el de cambio de selección.
     * Asegura sincronización entre la UI (chips) y el estado del ViewModel.
     */
    private void configurarListeners() {
        //cdo cambia el adapter
        adapter.setOnSeleccionCambioListener(actualizada -> {
            vmNuevoTutor.setDestinatariosSeleccionados(actualizada);//guardo la lista real
            vmNuevoTutor.sincronizarChips();                        //chips sobre lista actualizada
        });
    }

    /**
     * Configura botones, adjuntos y funcionalidad de envío una vez creada la vista.
     * @param view Vista raíz del fragmento.
     * @param savedInstanceState Estado previamente guardado.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarFooter();
        observarArchivos(vmNuevoTutor);
        obtenerLauncher(vmNuevoTutor);
        configurarBotonEnviar(view.findViewById(R.id.btnEnviar));
    }

    /**
     * Inicializa los elementos visuales del footer del layout,
     * incluyendo los componentes de categoría, mensaje y adjuntos.
     * También configura el botón de adjuntar archivos.
     */
    private void inicializarFooter() {
        this.tvCategoria = binding.contenedorFooter.layoutFooter.tvCategoria;
        this.editMensaje = binding.contenedorFooter.layoutFooter.editMensaje;
        layoutAdjuntos = binding.contenedorFooter.layoutAdjuntos;

        binding.contenedorFooter.layoutFooter.iconAdjunto.setOnClickListener(v -> lanzarSelectorArchivos());
    }

    /**
     * Configura la acción del botón "Enviar".
     * Cuando se presiona, se crea un objeto MensajeTutorDto con el mensaje actual,
     * se envía al servidor y se contrae la lista de mensajes en el adaptador.
     *
     * @param btnEnviar el botón que dispara el envío de mensaje.
     */
    private void configurarBotonEnviar(ImageButton btnEnviar) {
        btnEnviar.setOnClickListener(v -> {
            String mensaje = editMensaje.getText().toString().trim();
           Log.d("MensajeTutor", "Mensaje: " + mensaje+" Etiqueta: "+idEtiqueta+" Alumno: "+idAlumno);
            MensajeTutorDto dto = vmNuevoTutor.construirPayloadTutor(mensaje, idEtiqueta, idAlumno);
            Log.d("MensajeTutor", dto.toString());
            vmNuevoTutor.enviarMensajeTutor(getContext(), dto);
            adapter.contraerLista();
        });
    }

    /**
     * Vuelve a asignar el ViewModel luego de la creación de la actividad, si es necesario.
     * @param savedInstanceState Estado previamente guardado.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vmNuevoTutor = new ViewModelProvider(this).get(NuevoMensajeTutorViewModel.class);
    }

}