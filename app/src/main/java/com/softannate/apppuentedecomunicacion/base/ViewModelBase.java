package com.softannate.apppuentedecomunicacion.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.data.api.ApiService;
import com.softannate.apppuentedecomunicacion.data.api.Endpoints;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.data.local.TokenExpirationChecker;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.utils.SingleLiveEvent;
import java.util.List;

/**
 * Clase abstracta base para ViewModels en la aplicación.
 * <p>
 * Proporciona propiedades y servicios comunes como gestión de sesión,
 * control de tokens, acceso a endpoints, y manejo de LiveData compartidos.
 */
public abstract class
ViewModelBase extends AndroidViewModel {
    protected final SpManager spManager;
    protected final Endpoints endpoints;
    protected final TokenExpirationChecker tokenExpirationChecker;
   // private MutableLiveData<Boolean> mostrarMenu = new MutableLiveData<>(false);
    protected final MutableLiveData<String> mensaje = new MutableLiveData<>();
    //protected final MutableLiveData<Boolean> mostrarEstablecimiento = new MutableLiveData<>(false);
    //protected final MutableLiveData<Boolean> mostrarCursos = new MutableLiveData<>(false);
     protected MutableLiveData<List<MensajeDTO>> listaMensajes;
    protected MutableLiveData<Boolean> enviandoMensaje = new MutableLiveData<>();
     protected SingleLiveEvent<Boolean> limpiar = new SingleLiveEvent<>();

    //private String rol;
    protected int establecimiento, curso, establecimientoMensaje;
    //protected int unico = -1;

    /**
     * Constructor base para inicializar los componentes compartidos del ViewModel.
     *
     * @param application contexto global de la aplicación
     */
    public ViewModelBase(@NonNull Application application) {
        super(application);
        try {
            Context context = application.getApplicationContext();
            SpManager.initialize(context);// Inicializa el gestor de SharedPreferences
            spManager = new SpManager(context); // Crea instancia local
            tokenExpirationChecker = new TokenExpirationChecker();// Validador de expiración de tokens
            endpoints = ApiService.getApi(spManager, context,tokenExpirationChecker);// Instancia de acceso a API
            // this.listaChat = new MutableLiveData<>();
        } catch (Exception e) {
            Log.e("ViewModelBase", "Error en constructor", e);
            throw new RuntimeException("Error al crear ViewModelBase", e);
        }
    }
    /**
     * LiveData para mostrar mensajes tipo Toast u otros.
     *
     * @return mensaje actual como cadena de texto
     */
    public LiveData<String> getMensaje() {
        return mensaje;
    }

    /**
     * LiveData que indica si se está enviando un mensaje o realizando una operación.
     *
     * @return true si hay proceso en ejecución, false si está disponible
     */
    public LiveData<Boolean> getEnviandoMensaje() {
        return enviandoMensaje;
    }

    /**
     * Evento único para solicitar limpieza de campos (por ejemplo, input de email).
     *
     * @return evento que notifica al observer cuando limpiar debe ejecutarse
     */
    public LiveData<Boolean> getLimpiar() {
        if(limpiar==null){
            limpiar= new SingleLiveEvent<Boolean>();
        }
        return limpiar;
    }
    /**
     * LiveData que contiene una lista de mensajes para mostrar en la vista.
     *
     * @return lista de objetos MensajeDTO
     */
    public LiveData<List<MensajeDTO>> getListaMensaje() {
        if (listaMensajes == null) {
            listaMensajes = new MutableLiveData<>();
        }
        return listaMensajes;
    }
}