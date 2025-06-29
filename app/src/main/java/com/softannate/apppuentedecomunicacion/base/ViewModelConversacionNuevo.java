package com.softannate.apppuentedecomunicacion.base;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.modelos.Categoria_Mensaje;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewModelConversacionNuevo extends ViewModelBase{

    private MutableLiveData<List<Categoria_Mensaje>> listaCategorias = new MutableLiveData<>();

    public ViewModelConversacionNuevo(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Categoria_Mensaje>> getCategorias() {
        return listaCategorias;
    }

    public void obtenerCategorias() {
        Call<List<Categoria_Mensaje>> call = endpoints.categorias();
        call.enqueue(new Callback<List<Categoria_Mensaje>>() {
            @Override
            public void onResponse(Call<List<Categoria_Mensaje>> call, retrofit2.Response<List<Categoria_Mensaje>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Categoria_Mensaje> categoria_mensajes = response.body();
                    listaCategorias.setValue(categoria_mensajes);
                    Log.d("ListaNombresViewModel", "Response: " + categoria_mensajes);
                } else {
                    Log.e("ListaNombresViewModel", "Error en la llamada: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Categoria_Mensaje>> call, Throwable throwable) {
                Log.e("ListaNombresViewModel", "Error en la llamada: " + throwable.getMessage());
            }
        });
    }

/*
    public void enviarMensaje(MensajeRequest mensajeRequest) {

        Log.d("mensajeRequest", "MensajeRequest: " + mensajeRequest);
        if (mensajeRequest.getMessage() == null || mensajeRequest.getMessage().trim().isEmpty()) {
            Log.e("NuevoMensaje", "El mensaje está vacío");
            mensaje.setValue("Debe escribir un mensaje");
            return;
        }

        if (mensajeRequest.getMsg_etiquetas_id() == 0) {
            Log.e("NuevoMensaje", "La etiqueta no está seleccionada");
            mensaje.setValue("Debe seleccionar una categoría");
            return;
        }
        if(mensajeRequest.getAlumnosSeleccionados().isEmpty()){
            mensaje.setValue("Debe seleccionar un destinatario");
        }

        enviandoMensaje.setValue(true);
        Call<ResponseBody> call = endpoints.nuevoMensaje(mensajeRequest);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                enviandoMensaje.setValue(false);
                if(response.isSuccessful()){
                    Log.d("NuevoMensaje5", "enviado: " + response.body());
                    mensaje.setValue("Enviando mensaje...");
                    limpiar.setValue(true);
                    Log.d("NuevoMensaje4", "mensaje: " + mensajeRequest);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                enviandoMensaje.setValue(false);
                Log.e("NuevoMensaje3", "Error en la llamada: " + throwable.getMessage());
            }
        });
    }

 */
}
