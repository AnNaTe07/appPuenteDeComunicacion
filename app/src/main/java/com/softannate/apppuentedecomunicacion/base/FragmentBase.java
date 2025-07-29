package com.softannate.apppuentedecomunicacion.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import com.softannate.apppuentedecomunicacion.data.api.WebSocketManager;
import com.softannate.apppuentedecomunicacion.utils.TecladoUtils;

/**
 * Clase base para fragments que se comunican con {@link ViewModelBase}
 * y gestionan la conexión con {@link WebSocketManager}.
 * <p>
 * Configura listeners de WebSocket, gestiona visibilidad de teclado, y observa mensajes emitidos por el ViewModel.
 *
 * @param <T> Tipo de ViewModel que extiende {@link ViewModelBase}
 */
public abstract class FragmentBase<T extends ViewModelBase> extends Fragment implements WebSocketManager.EstadoMensajeListener {

    /** Instancia del ViewModel específico asociado al fragmento */
    protected T viewModelBase;

    /**
     * Callback que se dispara cuando el estado de un mensaje cambia en el WebSocket.
     * Este método debe ser sobreescrito por subclases si se desea implementar lógica específica.
     *
     * @param mensajeId    ID del mensaje recibido
     * @param nuevoEstado  Estado actualizado del mensaje
     */
    @Override
    public void onEstadoActualizado(int mensajeId, int nuevoEstado) {
        // Intencionalmente vacío, subclases pueden sobreescribi
    }

    /**
     * Establece este fragmento como listener de cambios en el estado de mensajes del WebSocket.
     * Se ejecuta automáticamente al iniciar el ciclo de vida.
     */
    @Override
    public void onStart() {
        super.onStart();
        WebSocketManager.setEstadoMensajeListener(this);
    }

    /**
     * Remueve el listener de estado de mensajes del WebSocket cuando el fragmento se detiene.
     */
    @Override
    public void onStop() {
        super.onStop();
        WebSocketManager.clearEstadoMensajeListener();
    }

    /**
     * Se ejecuta cuando la vista del fragmento ha sido creada.
     * <p>
     * Inicializa la conexión al WebSocket, configura ocultado de teclado,
     * y enlaza observadores del ViewModel si está disponible.
     *
     * @param view               Vista raíz del fragmento
     * @param savedInstanceState Estado previo del fragmento, si existe
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebSocketManager.setContext(getContext());
        WebSocketManager.conectar();

        TecladoUtils.configurarOcultadoDeTeclado(view, requireActivity());
        if(viewModelBase != null) {
            configurarObservers(viewModelBase);
        }
    }

    /**
     * Observa el LiveData de mensajes del ViewModel y muestra su contenido como Toast.
     * <p>
     * Este método puede ser sobrescrito por subclases para incluir observadores adicionales.
     *
     * @param viewModel instancia del ViewModel asociado al fragmento
     */
    protected void configurarObservers(T viewModel) {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}