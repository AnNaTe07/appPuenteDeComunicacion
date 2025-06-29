package com.softannate.apppuentedecomunicacion.ui.main.mensaje.conversacion;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.base.ViewModelConversacionNuevo;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;

import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversacionViewModel extends ViewModelConversacionNuevo {

    //private MutableLiveData<List<MensajeDTO>> listaMensajes = new MutableLiveData<>();
    private int alumnoId, usuarioId;

    public ConversacionViewModel(@NonNull Application application) {
        super(application);
        obtenerCategorias();
    }
    public void setIds(int usuarioId, int alumnoId) {
        this.usuarioId = usuarioId;
        this.alumnoId = alumnoId;
        cargarMensajes();
    }

    public void cargarMensajes(){
        Call<List<MensajeDTO>> call;
        if (alumnoId == 0) {
            call= endpoints.conversacion(usuarioId);
        } else {
            call = endpoints.conversacionConAlumno(usuarioId, alumnoId);
        }

       // Call<List<MensajeDTO>> call = endpoints.conversacion(usuarioId, alumnoId);
        call.enqueue(new Callback<List<MensajeDTO>>() {
            @Override
            public void onResponse(Call<List<MensajeDTO>> call, Response<List<MensajeDTO>> response) {
                if(response.isSuccessful() && response.body() != null){
                    listaMensajes.setValue(response.body());
                    Log.d("ViewModelResponse333", "Mensajes cargados correctamente: " + response.body());
                }else{
                    Log.e("ViewModelResponseError", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable throwable) {
                Log.d("ViewModelResponseConnectionError", "Error de conexión: " + throwable.getMessage());
            }
        });
    }

    public void marcarMensajesComoLeidos(){
        Call<Void> call;
        if (alumnoId == 0) {
            call= endpoints.mensajesLeidos(usuarioId);
        } else {
            call = endpoints.mensajesLeidosConAlumnos(usuarioId, alumnoId);
        }
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("Leidos",""+response.code());
                }else{
                    Log.e("Leidos","ingreso al else"+response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("Leidos", "Error al marcar mensajes como leidos: " + throwable.getMessage());
            }
        });
    }
/*
    public void buscarMensajes(String query, Date fechaInicio, Date fechaFin) {
      Call<List<MensajeDTO>> call = endpoints.buscar(query, fechaInicio, fechaFin);
        call.enqueue(new Callback<List<MensajeDTO>>() {
            @Override
            public void onResponse(Call<List<MensajeDTO>> call, Response<List<MensajeDTO>> response) {
                if(response.isSuccessful() && response.body() != null){
                    listaMensajes.setValue(response.body());
                    Log.d("ViewModelResponse333", "Mensajes cargados correctamente: " + response.body());
                }else{
                    Log.e("ViewModelResponseError", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable throwable) {
                Log.d("ViewModelResponseConnectionError", "Error de conexión: " + throwable.getMessage());
            }
        });
    }

 */
}