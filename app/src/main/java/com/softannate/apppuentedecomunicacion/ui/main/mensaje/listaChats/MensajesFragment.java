package com.softannate.apppuentedecomunicacion.ui.main.mensaje.listaChats;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.FragmentBase;
import com.softannate.apppuentedecomunicacion.databinding.FragmentMensajesBinding;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.utils.DatePicker;
import com.softannate.apppuentedecomunicacion.utils.DateUtils;
import com.softannate.apppuentedecomunicacion.utils.FiltroMensajes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MensajesFragment extends FragmentBase {

    private MensajesViewModel vmViewModel;
    private FragmentMensajesBinding binding;
    private AdapterMensaje adapter;
    private SearchView sv;
    private EditText fechaInicio, fechaFin, fechaInicio2, fechaFin2;
    private Date fechaInicioGuardada;
    private Date fechaFinGuardada;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        inicializarComponentes();
        vmViewModel= new ViewModelProvider(this).get(MensajesViewModel.class);

        configurarObservers(vmViewModel);
        configurarSwitchArchivados();
        configurarRecyclerView();
        configurarSearchView();
        configurarDatePickers();
        ocultarIconoDeInicio();
        ocultarIconoDeFin();
        configurarObservadores();
        configurarTextWatchersFecha();
        configurarBotonesBorrado();

        return binding.getRoot();
    }
    /**
     * Inicializa los componentes visuales principales del fragment y asocia las vistas
     * mediante ViewBinding.
     */
    private void inicializarComponentes() {
        binding = FragmentMensajesBinding.inflate(getLayoutInflater());
        fechaInicio2 = binding.etFechaInicio2;
        fechaFin2 = binding.etFechaFin2;
        sv = binding.svBuscarMensaje;
        fechaInicio = binding.etFechaInicio;
        fechaFin = binding.etFechaFin;
    }

    /**
     * Configura el Switch que alterna entre chats archivados y no archivados.
     * También actualiza los colores de los TextView según el estado.
     */
    private void configurarSwitchArchivados() {
        Switch swArchivados = binding.swChatsArchivados;
        TextView tvIzquierda = binding.tvIzquierdaSwitch;
        TextView tvDerecha = binding.tvDerechaSwitch;

        swArchivados.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vmViewModel.setMostrarArchivados(isChecked);
           // tvIzquierda.setTextColor(isChecked ? Color.parseColor("#777777") : Color.parseColor("#333333"));
            //tvDerecha.setTextColor(isChecked ? Color.parseColor("#333333") : Color.parseColor("#777777"));
        });

        // Al tocar activa el switch manualmente
        tvIzquierda.setOnClickListener(v -> swArchivados.setChecked(false));
        tvDerecha.setOnClickListener(v -> swArchivados.setChecked(true));
    }

    /**
     * Inicializa el RecyclerView para mostrar los mensajes en orden invertido,
     * empezando desde el mensaje más reciente.
     */
    private void configurarRecyclerView() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        adapter = new AdapterMensaje(null, new ArrayList<>(), navController);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);     // Invierte el orden
        layoutManager.setStackFromEnd(true);      // Empieza desde el final (último mensaje)

        binding.rvMensajes.setLayoutManager(layoutManager);
        binding.rvMensajes.setAdapter(adapter);
    }

    /**
     * Configura el SearchView y lanza búsqueda cada vez que el texto cambia
     * o se confirma una consulta.
     */
    private void configurarSearchView() {
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ejecutarBusqueda();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ejecutarBusqueda();
                return true;
            }
        });
    }

    /**
     * Asocia DatePickers a los campos de fecha para permitir al usuario
     * seleccionar los límites de la búsqueda.
     */
    private void configurarDatePickers() {
        fechaInicio.setOnClickListener(view -> {
            // Mostrar el DatePicker para la fecha de inicio, limitando fechas posteriores a la fecha de fin
            DatePicker.showFechaInicioPicker(requireActivity(), fechaInicio);
        });

        fechaFin.setOnClickListener(view -> {
            // Mostrar el DatePicker para la fecha de fin, limitando fechas anteriores a la fecha de inicio
            DatePicker.showFechaFinPicker(requireActivity(), fechaFin);
        });
    }

    /**
     * Oculta el ícono de borrado en el campo auxiliar de fecha de inicio.
     */
    private void ocultarIconoDeInicio() {
        fechaInicio2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * Oculta el ícono de borrado en el campo auxiliar de fecha de fin.
     */
    private void ocultarIconoDeFin() {
        fechaFin2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * Observa cambios en el ViewModel para mostrar u ocultar filtros
     * y actualizar los resultados en el RecyclerView.
     */
    private void configurarObservadores() {
        vmViewModel.getMostrarFiltrosBusqueda().observe(getViewLifecycleOwner(), visible -> {
            sv.setVisibility(visible ? View.VISIBLE : View.GONE);
            fechaInicio.setVisibility(visible ? View.VISIBLE : View.GONE);
            fechaFin.setVisibility(visible ? View.VISIBLE : View.GONE);
        });


        vmViewModel.getResultados().observe(getViewLifecycleOwner(), resultado -> {
            List<MensajeDTO> mensajesOrdenados = resultado.getResultados();
            Collections.reverse(mensajesOrdenados); //nuevo arriba

            adapter.setMensajes(resultado.getFiltro(), mensajesOrdenados);
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Configura listeners en los campos de fecha para detectar cambios
     * y disparar la lógica de búsqueda en tiempo real.
     */
    private void configurarTextWatchersFecha() {
        fechaInicio.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                fechaInicioGuardada = DateUtils.parseFechaDesdeTexto(fechaInicio.getText().toString());
                ejecutarBusqueda();
                fechaInicio2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });


        fechaFin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                fechaFinGuardada = DateUtils.parseFechaDesdeTexto(fechaFin.getText().toString());
                ejecutarBusqueda();
                fechaFin2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    /**
     * Configura los botones de borrado (íconos en los campos auxiliares) para limpiar
     * los filtros de búsqueda y actualizar los resultados.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void configurarBotonesBorrado() {
        fechaInicio2.setOnTouchListener((v, event) -> {
            FiltroMensajes.limpiarFiltros(fechaInicio, null);
            DatePicker.resetFechaInicio();
            ejecutarBusqueda();
            ocultarIconoDeInicio();
            return true;
        });

        fechaFin2.setOnTouchListener((v, event) -> {
            FiltroMensajes.limpiarFiltros(null, fechaFin);
            DatePicker.resetFechaFin();
            ejecutarBusqueda();
            ocultarIconoDeFin();
            return true;
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personalizarSearchView();
    }

    /**
     * Personaliza la apariencia del SearchView:
     * - Cambia el color del ícono de búsqueda (lupa)
     * - Cambia el color del texto ingresado
     * - Cambia el color del ícono de limpiar texto (X)
     */
    private void personalizarSearchView() {
        // Ícono de búsqueda (lupa)
        ImageView searchIcon = sv.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorLupa), PorterDuff.Mode.SRC_IN);

        // Campo de texto del SearchView
        EditText searchEditText = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

        // Ícono de limpiar texto (X)
        ImageView clearIcon = sv.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vmViewModel = new ViewModelProvider(this).get(MensajesViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reestablecer íconos de fecha si los campos están vacíos
        limpiarIconosFechaSiSonVacios();

        // Ejecutar búsqueda según filtros actuales
        ejecutarBusqueda();

        // Refrescar vista de mensajes
        adapter.notifyDataSetChanged();
    }

    /**
     * Remueve los íconos (x)de los campos de fecha si están vacíos.
     */
    private void limpiarIconosFechaSiSonVacios() {
        if (fechaInicio.getText().toString().isEmpty()) {
          ocultarIconoDeInicio();
        }
        if (fechaFin.getText().toString().isEmpty()) {
         ocultarIconoDeFin();
        }
    }

    /**
     * Llama al ViewModel para realizar búsqueda con los filtros actuales.
     */
    private void ejecutarBusqueda() {
        String texto = sv.getQuery() != null ? sv.getQuery().toString().trim() : "";
        vmViewModel.eleccionChats(texto, fechaInicioGuardada, fechaFinGuardada);
    }

}


