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


public abstract class
ViewModelBase extends AndroidViewModel {
    protected final SpManager spManager;
    protected final Endpoints endpoints;
    protected final TokenExpirationChecker tokenExpirationChecker;
    private MutableLiveData<Boolean> mostrarMenu = new MutableLiveData<>(false);
    // private MutableLiveData<MenuAction> mostrarMenuAction = new MutableLiveData<>();
    protected final MutableLiveData<String> mensaje = new MutableLiveData<>();
    // protected final MutableLiveData<List<AlumnoDto>> listaAlumnos = new MutableLiveData<>();
    protected final MutableLiveData<Boolean> mostrarEstablecimiento = new MutableLiveData<>(false);
    protected final MutableLiveData<Boolean> mostrarCursos = new MutableLiveData<>(false);
     protected MutableLiveData<List<MensajeDTO>> listaMensajes;
    protected MutableLiveData<Boolean> enviandoMensaje = new MutableLiveData<>();
     protected SingleLiveEvent<Boolean> limpiar = new SingleLiveEvent<>();
    // protected MutableLiveData<EstablecimientoConCursos> listaEstablecimiento;

    private String rol;
    protected int establecimiento, curso, establecimientoMensaje;
    protected int unico = -1;
    // protected final SingleLiveEvent<ListaNombresViewModel.NavegacionConOpciones> navegarConOpciones = new SingleLiveEvent<>();

    public ViewModelBase(@NonNull Application application) {
        super(application);
        try {
            Context context = application.getApplicationContext();
            SpManager.initialize(context);
            spManager = new SpManager(context);
            tokenExpirationChecker = new TokenExpirationChecker();
            endpoints = ApiService.getApi(spManager, context,tokenExpirationChecker);
            // this.listaChat = new MutableLiveData<>();
        } catch (Exception e) {
            Log.e("ViewModelBase", "Error en constructor", e);
            throw new RuntimeException("Error al crear ViewModelBase", e);
        }
    }
    public LiveData<String> getMensaje() {
        return mensaje;
    }
    public LiveData<Boolean> getEnviandoMensaje() {
        return enviandoMensaje;
    }
    public LiveData<Boolean> getLimpiar() {
        if(limpiar==null){
            limpiar= new SingleLiveEvent<Boolean>();
        }
        return limpiar;
    }
    public LiveData<List<MensajeDTO>> getListaMensaje() {
        if (listaMensajes == null) {
            listaMensajes = new MutableLiveData<>();
        }
        return listaMensajes;
    }
}