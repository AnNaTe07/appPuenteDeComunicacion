package com.softannate.apppuentedecomunicacion.ui.main.mensaje.listaChats;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.base.ViewModelBase;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.modelos.dto.ResultadoBusquedaDto;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MensajesViewModel extends ViewModelBase {

    private Context context;
    private MutableLiveData<List<MensajeDTO>> listaMensajes;
    private MutableLiveData<Boolean> mostrarMensajes;
    private MutableLiveData<ResultadoBusquedaDto> resultados = new MutableLiveData<>();
    private List<MensajeDTO> listaFiltrada;

    public MensajesViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        //visibility = new MutableLiveData<>();
        mostrarMensajes = new MutableLiveData<>(false);
      //  obtenerRol();
    }

    public LiveData<List<MensajeDTO>> getMensajes() {
        if (listaMensajes == null) {
            listaMensajes = new MutableLiveData<>();
        }
        return listaMensajes;
    }

    public LiveData<Boolean> getMostrarMensajes() {
        if (mostrarMensajes == null) {
            mostrarMensajes = new MutableLiveData<>();
        }
        return mostrarMensajes;
    }

    public LiveData<ResultadoBusquedaDto> getResultados() {
        return resultados;
    }

    public void obtenerMensajes() {
        Call<List<MensajeDTO>> call = endpoints.mensajes();
        call.enqueue(new Callback<List<MensajeDTO>>() {
            @Override
            public void onResponse(Call<List<MensajeDTO>> call, Response<List<MensajeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MensajeDTO> chats = response.body();

                   // listaMensajes.setValue(chats);
                    resultados.setValue(new ResultadoBusquedaDto("", chats));
                    if(response.body().isEmpty()){
                        mensaje.setValue("Usted no recibió mensajes");
                    }
                    Log.d("ViewModelResponse222", "Mensajes cargados correctamente: " + response.body());
                } else {
                    mensaje.setValue("Error al obtener la lista de chats: " + response.message());
                    Log.e("ViewModelResponseError", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable t) {
                //mensaje.setValue("Error de conexión: " + t.getMessage());
                Log.e("ViewModelResponseConnectionError", "Error de conexión: " + t.getMessage(), t);
            }
        });
    }

    public void buscarMensajes(String query, Date fechaInicioD, Date fechaFinD) {

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaInicio = fechaInicioD != null ? formato.format(fechaInicioD) : null;
        String fechaFin = fechaFinD != null ? formato.format(fechaFinD) : null;


        Log.d("buscarMensajes", "Buscando mensajes con query: " + query + ", fechaInicio: " + fechaInicio + ", fechaFin: " + fechaFin);
        Call<List<MensajeDTO>> buscar;


        if (fechaInicio != null && fechaFin == null) {
            buscar = endpoints.buscar(query,  fechaInicio, null); // Solo fechaInicio
        } else if (fechaFin != null && fechaInicio == null) {
            buscar = endpoints.buscar( query, null, fechaFin); // Solo fechaFin
        } else if (fechaInicio != null && fechaFin != null) {
            buscar = endpoints.buscar( query,  fechaInicio, fechaFin); // Ambas fechas
        } else {
            buscar = endpoints.buscar(query,  null, null); // Ninguna fecha
        }

        buscar.enqueue(new Callback<List<MensajeDTO>>() {
            @Override
            public void onResponse(Call<List<MensajeDTO>> call, Response<List<MensajeDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        Log.d("buscarMensajes", "Mensajes recibidos: " + response.body().toString());
                        listaFiltrada = response.body();
                        //listaMensajes.setValue(listaFiltrada);
                        resultados.setValue(new ResultadoBusquedaDto(query, listaFiltrada));
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "sin cuerpo";
                            Log.e("buscarMensajes", "Error HTTP " + response.code() + ": " + error);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        resultados.setValue(new ResultadoBusquedaDto(query, new ArrayList<>()));
                        //listaMensajes.postValue(new ArrayList<>());
                        mensaje.setValue("No se encontraron mensajes");
                    }

                } else {
                    resultados.setValue(new ResultadoBusquedaDto(query, new ArrayList<>()));
                    //listaMensajes.postValue(new ArrayList<>());
               mensaje.setValue("Error al buscar mensajes"); }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable throwable) {
                Log.d("error buscar", "Error en el servidor", throwable);
            }
        });
    }
}


