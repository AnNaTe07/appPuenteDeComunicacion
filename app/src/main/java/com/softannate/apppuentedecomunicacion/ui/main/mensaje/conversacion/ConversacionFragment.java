package com.softannate.apppuentedecomunicacion.ui.main.mensaje.conversacion;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.FragmentNuevoConversacion;
import com.softannate.apppuentedecomunicacion.data.api.WebSocketManager;
import com.softannate.apppuentedecomunicacion.databinding.FragmentConversacionBinding;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.utils.ArchivoAdjuntoHelper;
import java.util.ArrayList;
import java.util.List;

public class ConversacionFragment extends FragmentNuevoConversacion implements WebSocketManager.EstadoMensajeListener {

    private ConversacionViewModel vmConversacion;
    private ConversacionAdapter adapter;
    private FragmentConversacionBinding binding;
    private RecyclerView rvConversaciones;
    private List<MensajeDTO> listaMensajes;
    private static final int TIEMPO_ESPERA = 4000;
    private int paginaActual = 0;
    private final int pageSize = 35;
    private boolean cargando = false;
    private boolean finDeConversacion = false; // para bloquear la carga al final


    /**
     * Método del ciclo de vida del Fragment que infla la vista y configura componentes clave.
     *
     * <p>Se enlaza el binding del layout, se crea el ViewModel, y se configuran observadores,
     * la lógica inicial, el RecyclerView y otros observadores. Devuelve la raíz de la vista.</p>
     *
     * @param inflater Objeto utilizado para inflar el layout del Fragment.
     * @param container Contenedor padre del Fragment.
     * @param savedInstanceState Estado previamente guardado (puede ser nulo).
     * @return La vista raíz inflada del Fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding= FragmentConversacionBinding.inflate(inflater, container, false);
        vmConversacion= new ViewModelProvider(this).get(ConversacionViewModel.class);
        configurarObservers(vmConversacion);
        inicializarLogica();
        configurarRecyclerView();
        configurarObservadores();

        return binding.getRoot();
    }

    /**
     * Inicializa la lógica relacionada con el ViewModel y los servicios WebSocket.
     *
     * <p>Establece el launcher para enviar datos y configura el listener para
     * cambios de estado en mensajes vía WebSocket.</p>
     */
    private void inicializarLogica() {
        obtenerLauncher(vmConversacion);
        WebSocketManager.setEstadoMensajeListener(this);
    }

    /**
     * Configura el RecyclerView que muestra los mensajes en el chat.
     *
     * <ul>
     *   <li>Inicializa la lista de mensajes.</li>
     *   <li>Define el layout vertical y que el scroll se mantenga en el final.</li>
     *   <li>Establece el adaptador para manejar los mensajes.</li>
     * </ul>
     */
    private void configurarRecyclerView() {
        listaMensajes = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);

        rvConversaciones = binding.recyclerMensajes;
        rvConversaciones.setLayoutManager(layoutManager);
        rvConversaciones.setAdapter(adapter);
    }

    /**
     * Observa cambios en el estado del ViewModel para actualizar la interfaz.
     *
     * <ul>
     *   <li>Visibilidad del footer para escribir mensajes.</li>
     *   <li>Estado del botón de envío mientras se realiza el proceso de envío.</li>
     * </ul>
     */
    private void configurarObservadores() {
        vmConversacion.mostrarLayout().observe(getViewLifecycleOwner(), mostrar ->
                binding.contenedorFooter.layoutFooter.footEscribir.setVisibility(mostrar ? View.VISIBLE : View.GONE)
        );

        vmConversacion.getEnviandoMensaje().observe(getViewLifecycleOwner(), enviando ->
                binding.contenedorFooter.layoutFooter.btnEnviar.setEnabled(!enviando)
        );
    }


    /**
     * Método del ciclo de vida del Fragment que se ejecuta después de que la vista ha sido creada.
     *
     * <p>Inicializa la interfaz, configura los datos de usuario y alumno desde los argumentos,
     * observa cambios de estado del ViewModel y establece los listeners para scroll, envío de mensajes,
     * descarga de archivos y marcado de mensajes como leídos.</p>
     *
     * @param view Vista raíz del fragment.
     * @param savedInstanceState Estado guardado (no utilizado en este caso).
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inicializarUI();
        Bundle args = getArguments();
        if (args == null) return;

        inicializarDatos(args);
        configurarObservers();
        configurarScrollListener();
        configurarAccionesFooter(args);
        observarDescargaArchivos();
        marcarMensajesLeidosConDelay();


    }

    /**
     * Inicializa las referencias a elementos visuales de la interfaz, como los campos de mensaje,
     * la categoría y el contenedor de adjuntos.
     */
    private void inicializarUI() {
            tvCategoria = binding.contenedorFooter.layoutFooter.tvCategoria;
            editMensaje = binding.contenedorFooter.layoutFooter.editMensaje;
            layoutAdjuntos = binding.contenedorFooter.layoutAdjuntos;
        }

    /**
     * Carga los datos iniciales desde el {@code Bundle} de argumentos.
     *
     * <p>Establece los textos de usuario y alumno, carga el avatar del usuario, y
     * llama a métodos del ViewModel para inicializar su estado y cargar la primera página de mensajes.</p>
     *
     * @param args Bundle con argumentos de navegación que contienen información del usuario y alumno.
     */
    private void inicializarDatos(Bundle args) {
        binding.nombreUsuario.setText(args.getString("userNombre"));
        binding.nombreAlumno.setText(args.getString("alumnoNombre"));

        Glide.with(requireContext())
                .load(getArguments().getString("avatar"))
                .placeholder(R.drawable.cargando)//imagen temporal mientras se carga
                .diskCacheStrategy(DiskCacheStrategy.NONE)//desactivo la cache
                .skipMemoryCache(true)
                .error(R.drawable.perfil_user)
                .into(binding.fotoUsuario);


        vmConversacion.inicializarDesdeBundle(requireContext(), args);
        vmConversacion.cargarMensajes(paginaActual);
    }

    /**
     * Configura los observadores de LiveData expuestos por el ViewModel.
     *
     * <ul>
     *   <li>Observa si se deben limpiar los campos y recargar los mensajes.</li>
     *   <li>Observa los cambios en la lista de mensajes y actualiza el RecyclerView.</li>
     *   <li>Observa actualizaciones puntuales en mensajes y refresca los ítems.</li>
     * </ul>
     */
    private void configurarObservers() {
        vmConversacion.getLimpiar().observe(getViewLifecycleOwner(), limpiar -> {
            if (limpiar) {
                limpiarCampos();
                vmConversacion.cargarMensajes(paginaActual);
            }
        });

        vmConversacion.getListaMensaje().observe(getViewLifecycleOwner(), mensajes -> {
            if (adapter == null) {
                adapter = new ConversacionAdapter(mensajes, vmConversacion.getIdVisual());
                rvConversaciones.setAdapter(adapter);
                adapter.setOnArchivoClickListener(archivo ->
                        vmConversacion.descargarYAbrirArchivo(requireContext(), archivo));
            } else {
                adapter.setListaMensajes(mensajes);
            }

            listaMensajes.clear();
            listaMensajes.addAll(mensajes);
            rvConversaciones.scrollToPosition(listaMensajes.size() - 1);
        });

        vmConversacion.getMensajeActualizadoIndex().observe(getViewLifecycleOwner(), index -> {
            adapter.notifyItemChanged(index);
        });

        observarArchivos(vmConversacion);
    }

    /**
     * Configura el comportamiento de scroll del RecyclerView que muestra los mensajes.
     *
     * <p>Cuando el usuario alcanza el inicio de la lista y aún hay más mensajes por cargar,
     * se incrementa la página actual y se solicita la siguiente tanda de mensajes.</p>
     */
    private void configurarScrollListener() {
        rvConversaciones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem == 0 && !cargando && !finDeConversacion) {
                    cargando = true;
                    paginaActual++;
                    vmConversacion.cargarMensajes(paginaActual);
                }
            }
        });
    }

    /**
     * Configura los eventos de los botones del footer de conversación.
     *
     * <ul>
     *   <li>El botón de adjunto permite abrir el selector de archivos.</li>
     *   <li>El botón de enviar crea un mensaje nuevo y lo envía a través del ViewModel,
     *       utilizando los datos del receptor, alumno y rol extraídos del Bundle.</li>
     * </ul>
     *
     * @param args Bundle con datos del usuario y alumno involucrado en la conversación.
     */
    private void configurarAccionesFooter(Bundle args) {
        binding.contenedorFooter.layoutFooter.iconAdjunto.setOnClickListener(v -> lanzarSelectorArchivos());

        //enviar mensaje
        binding.contenedorFooter.layoutFooter.btnEnviar.setOnClickListener(v -> {
            int usuario = args.getInt("receptorId");
            int alumno = args.getInt("alumnoId");
            int rol = args.getInt("rol");

            vmConversacion.prepararDestinatarios(usuario, alumno, rol);
            //String mensaje = binding.contenedorFooter.layoutFooter.editMensaje.getText().toString().trim();

            String mensaje = editMensaje.getText().toString().trim();
            vmConversacion.enviarMensaje(requireContext(), mensaje, idEtiqueta, alumno, rol);
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Observa el LiveData que notifica cuando se ha descargado un archivo.
     *
     * <p>Una vez descargado, se abre automáticamente con el helper utilizando el tipo MIME detectado.</p>
     */
    private void observarDescargaArchivos() {
        vmConversacion.getArchivoDescargado().observe(getViewLifecycleOwner(), pair -> {
            if (pair != null) {
                ArchivoAdjuntoHelper.abrir(requireContext(), pair.first, pair.second);//archivo local, mimeType
            }
        });
    }

    /**
     * Marca todos los mensajes actuales como leídos después de un retardo predefinido.
     *
     * <p>Esta acción se ejecuta con un {@code Handler} para dar tiempo a que la vista se estabilice
     * antes de modificar el estado de los mensajes.</p>
     */
    private void marcarMensajesLeidosConDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            vmConversacion.marcarMensajesComoLeidos();
            Log.d("ViewModelResponse2", "mensajes marcados como leidos");
        }, TIEMPO_ESPERA);
    }

    /**
     * Implementación del listener que recibe actualizaciones del estado de los mensajes
     * desde WebSocketManager.
     *
     * @param mensajeId ID del mensaje cuyo estado ha cambiado.
     * @param nuevoEstado Estado actualizado recibido por WebSocket.
     */
    @Override
    public void onEstadoActualizado(int mensajeId, int nuevoEstado) {
        vmConversacion.actualizarEstadoMensaje(mensajeId, nuevoEstado);
    }

    /**
     * Ciclo de vida: onActivityCreated.
     *
     * <p>Inicializa el ViewModel de conversación para vincularlo al fragmento.</p>
     *
     * @param savedInstanceState Estado guardado (no utilizado aquí).
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vmConversacion = new ViewModelProvider(this).get(ConversacionViewModel.class);
    }

    /**
     * Ciclo de vida: onDestroy.
     *
     * <p>Limpia el listener de WebSocket al destruir el fragmento para evitar fugas de memoria.</p>
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketManager.clearEstadoMensajeListener();
    }

}