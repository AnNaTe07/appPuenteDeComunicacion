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
import com.softannate.apppuentedecomunicacion.utils.Fecha;

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

/**
 * ViewModel encargado de obtener, filtrar y exponer los mensajes del usuario.
 * Utiliza LiveData para comunicar los cambios al UI de forma reactiva.
 */

public class MensajesViewModel extends ViewModelBase {

   // private Context context;
   // private MutableLiveData<List<MensajeDTO>> listaMensajes;
    //private MutableLiveData<Boolean> mostrarMensajes;
    private MutableLiveData<ResultadoBusquedaDto> resultados = new MutableLiveData<>();
    private List<MensajeDTO> listaFiltrada;
    private static final String TAG = "obtenerMensajes";
    private static final String TAG2 = "buscarMensajes";



    public MensajesViewModel(@NonNull Application application) {
        super(application);
       // this.context = application.getApplicationContext();
        //visibility = new MutableLiveData<>();
        //mostrarMensajes = new MutableLiveData<>(false);
      //  obtenerRol();
    }
/*
    public LiveData<List<MensajeDTO>> getMensajes() {
        if (listaMensajes == null) {
            listaMensajes = new MutableLiveData<>();
        }
        return listaMensajes;
    }

 */
/*
    public LiveData<Boolean> getMostrarMensajes() {
        if (mostrarMensajes == null) {
            mostrarMensajes = new MutableLiveData<>();
        }
        return mostrarMensajes;
    }

 */

    public LiveData<ResultadoBusquedaDto> getResultados() {
        return resultados;
    }

    /**
     * Obtiene todos los mensajes disponibles desde el endpoint.
     * Actualiza el LiveData 'resultados' con los mensajes.
     */
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
                    Log.d("TAG", "Mensajes cargados correctamente: " + response.body());
                } else {
                    mensaje.setValue("Error al obtener la lista de chats: " + response.message());
                    Log.e("TAG", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable t) {
                Log.e("TAG", "Error de conexión: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Busca mensajes por texto y rango de fechas.
     *
     * @param query Texto ingresado por el usuario
     * @param fechaInicioD Fecha de inicio del filtro (puede ser null)
     * @param fechaFinD Fecha de fin del filtro (puede ser null)
     */
    public void buscarMensajes(String query, Date fechaInicioD, Date fechaFinD) {

       // SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaInicio = Fecha.formatearFecha(fechaInicioD);
        String fechaFin = Fecha.formatearFecha(fechaFinD);


        Log.d("TAG2", "Buscando mensajes con query: " + query + ", fechaInicio: " + fechaInicio + ", fechaFin: " + fechaFin);
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
                        Log.d("TAG2", "Mensajes recibidos: " + response.body().toString());
                        listaFiltrada = response.body();
                        //listaMensajes.setValue(listaFiltrada);
                        resultados.setValue(new ResultadoBusquedaDto(query, listaFiltrada));
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "sin cuerpo";
                            Log.e("TAG2", "Error HTTP " + response.code() + ": " + error);
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
                    Log.e("TAG2", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable throwable) {
                Log.e("TAG2", "Error en el servidor", throwable);
            }
        });
    }

    /**
     * Obtiene la lista de mensajes archivados desde el servidor de forma asíncrona.
     *
     * <p>Si la respuesta es exitosa y contiene datos, los mensajes se guardan en
     * el resultado de búsqueda. Si no hay mensajes, se muestra un mensaje informativo
     * al usuario. En caso de error o fallo en la conexión, se registran los errores
     * correspondientes en el log.</p>
     */
    public void obtenerArchivados() {
        Call<List<MensajeDTO>> call = endpoints.obtenerArchivados();
        call.enqueue(new Callback<List<MensajeDTO>>() {
            @Override
            public void onResponse(Call<List<MensajeDTO>> call, Response<List<MensajeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MensajeDTO> chats = response.body();

                    // listaMensajes.setValue(chats);
                    resultados.setValue(new ResultadoBusquedaDto("", chats));
                    if(response.body().isEmpty()){
                        mensaje.setValue("Usted no tiene mensajes archivados");
                    }
                    Log.d("TAG", "Mensajes cargados correctamente: " + response.body());
                } else {
                    mensaje.setValue("Error al obtener la lista de chats: " + response.message());
                    Log.e("TAG", "Código de error: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable t) {
                Log.e("TAG", "Error de conexión: " + t.getMessage(), t);
            }
        });
    }

    private final MutableLiveData<Boolean> mostrarArchivados = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> mostrarFiltrosBusqueda = new MutableLiveData<>(true);

    /**
     * Devuelve un {@code LiveData} que indica si los filtros de búsqueda están visibles.
     *
     * @return LiveData<Boolean> indicando visibilidad de los filtros
     */
    public LiveData<Boolean> getMostrarFiltrosBusqueda() {
        return mostrarFiltrosBusqueda;
    }

    /**
     * Actualiza el estado de visualización de los chats archivados.
     *
     * <p>Cuando se activa, se ocultan los filtros de búsqueda y se cargan los mensajes
     * archivados. Cuando se desactiva, se muestran los filtros y se cargan los mensajes activos.</p>
     *
     * @param valor `true` para mostrar archivados, `false` para mostrar activos
     */
    public void setMostrarArchivados(boolean valor) {
        mostrarArchivados.setValue(valor);
        if (valor) {
            obtenerArchivados();
            mostrarFiltrosBusqueda.setValue(false);
        } else {
            obtenerMensajes();
            mostrarFiltrosBusqueda.setValue(true);
        }
    }

    /**
     * Decide si se deben aplicar filtros de búsqueda o mostrar todos los mensajes.
     *
     * <p>Si se proporciona algún texto o fecha, realiza una búsqueda filtrada.
     * De lo contrario, carga todos los mensajes disponibles.</p>
     *
     * @param texto Término de búsqueda textual
     * @param fechaInicio Fecha de inicio para el rango de búsqueda
     * @param fechaFin Fecha de fin para el rango de búsqueda
     */
    public void eleccionChats(String texto, Date fechaInicio, Date fechaFin) {
            boolean hayFiltros = (texto != null && !texto.trim().isEmpty()) || fechaInicio != null || fechaFin != null;

            if (hayFiltros) {
                buscarMensajes(texto, fechaInicio, fechaFin);
            } else {
                obtenerMensajes();
            }
    }
}


