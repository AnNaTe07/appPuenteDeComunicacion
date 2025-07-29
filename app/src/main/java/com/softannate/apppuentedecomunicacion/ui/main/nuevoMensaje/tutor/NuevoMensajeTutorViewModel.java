package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.tutor;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.base.ViewModelConversacionNuevo;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.ChipDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NuevoMensajeTutorViewModel extends ViewModelConversacionNuevo {

    private MutableLiveData<List<AlumnoDto>> alumnos = new MutableLiveData<>();
    private MutableLiveData<Boolean> mostrarVista = new MutableLiveData<>(false);
    private List<AlumnoDto> alumnosList = new ArrayList<>();

    //para cargar el autocomplete de nombres
    private final MutableLiveData<List<String>> nombresAlumnos = new MutableLiveData<>();

    //PARA MOSTRAR DATOS A 1 ALUMNO
    private MutableLiveData<Boolean> mostrarDestinatarios = new MutableLiveData<>(false);
    private int idAlumno;

    //para chips
    private final MutableLiveData<List<ChipDto>> chips = new MutableLiveData<>(new ArrayList<>());


    //--------------------------------------Constructor---------------------------------------
    /**
     * Constructor del ViewModel.
     * @param application Aplicación.
     */
    public NuevoMensajeTutorViewModel(@NonNull Application application) {
        super(application);
        //cargarAlumnos();
        obtenerCategorias();

        List<AlumnoDto> listaAlumnos = SpManager.getAlumnosList(application);
        //si solo hay 1 alumno
        if (listaAlumnos != null && listaAlumnos.size() == 1) {
            idAlumno = listaAlumnos.get(0).getId();
            mostrarDestinatarios.setValue(true);
        }
        cargarAlumnos();
    }

    //--------------------------------------------LiveData-----------------------------------

    public LiveData<List<String>> getNombresAlumnos() { return nombresAlumnos; }

    public LiveData<List<DestinatarioDto>> getDestinatarios() {
        return destinatarios;
    }

    /**
     * Devuelve si deben mostrarse los destinatarios. Esto ocurre cuando hay un solo alumno.
     * @return LiveData que indica visibilidad de los destinatarios.
     */
    public LiveData<Boolean> getMostrarDestinatarios() {
        return mostrarDestinatarios;
    }

    public LiveData<List<AlumnoDto>> getAlumnos() {
        return alumnos;
    }

    public LiveData<Boolean> getMostrarVista() {
        return mostrarVista;
    }

    //------------------------------Estado y selección de alumno único---------------------------

    /**
     * Devuelve el ID del alumno único si aplica.
     * @return ID del alumno seleccionado automáticamente.
     */
    public int getId() {
        return this.idAlumno;
    }


    //------------------------------------Carga de alumnos------------------------------------
    /**
     * Carga la lista de alumnos desde caché local o endpoint remoto si es necesario.
     * Actualiza los alumnos visibles y los nombres para autocomplete.
     */
    public void cargarAlumnos() {
        List<AlumnoDto> cache = SpManager.getAlumnosList(getApplication());
        if (cache != null && !cache.isEmpty()) {
            alumnosList = cache;
            alumnos.setValue(cache);
            List<String> nombres = new ArrayList<>();
            for (AlumnoDto alumno : alumnosList) {
                nombres.add(alumno.getNombre());
            }
            nombresAlumnos.setValue(nombres);
            mostrarVista.setValue(cache.size() > 1);
            Log.d("ViewModelNuevoTutor", "Alumnos recuperados desde SharedPreferences");
            return;
        }


        Call<List<AlumnoDto>> call = endpoints.alumnos();
        call.enqueue(new Callback<List<AlumnoDto>>() {
            @Override
            public void onResponse(Call<List<AlumnoDto>> call, Response<List<AlumnoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    alumnosList = response.body();
                    alumnos.setValue(alumnosList);
                    List<String> nombres = new ArrayList<>();
                    for (AlumnoDto alumno : alumnosList) {
                        nombres.add(alumno.getNombre());
                    }
                    nombresAlumnos.setValue(nombres);
                    mostrarVista.setValue(alumnosList.size() > 1);
                    SpManager.setAlumnosList(getApplication(), alumnosList);
                    Log.d("ViewModelNuevoTutor", "Alumnos cargados correctamente");
                } else {
                    Log.e("ViewModelNuevoTutor", "Error de carga: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AlumnoDto>> call, Throwable t) {
                alumnos.setValue(Collections.emptyList());
                Log.e("ViewModelNuevoTutor", "Error de conexión: " + t.getMessage());
            }
        });
    }

    //para seleccionar id de alumno si hay mas de 1
    /**
     * Devuelve el ID de alumno que coincide con el nombre especificado.
     * @param nombre Nombre completo del alumno.
     * @return ID correspondiente si se encuentra; null en caso contrario.
     */
    public Integer getIdPorNombre(String nombre) {
        for (AlumnoDto alumno : alumnosList) {
            if (alumno.getNombre().equals(nombre)) {
                return alumno.getId();
            }
        }
        return null; // o lanzar una excepción para forzar que siempre haya coincidencia
    }

    /**
     * Devuelve los chips actuales representando destinatarios seleccionados.
     * @return LiveData de chips activos.
     */
    public LiveData<List<ChipDto>> getChips() {
        return chips;
    }

    /**
     * Reconstruye los chips visuales en base a la selección de destinatarios.
     * Incluye chips agrupados como "Todos los destinatarios" o individuales.
     */
    public void sincronizarChips() {
        List<ChipDto> resultado = new ArrayList<>();

        //  DESTINATARIOS
        List<DestinatarioDto> destinatariosLista = getDestinatarios().getValue();
        if (destinatariosLista == null) destinatariosLista = new ArrayList<>();

        Set<Integer> destinatariosMarcados = new HashSet<>();

        if (destinatariosLista != null && !destinatariosLista.isEmpty()) {
            boolean todosDestinatariosSeleccionados = true;
            for (DestinatarioDto d : destinatariosLista) {
                if (!d.isSeleccionado()) {
                    todosDestinatariosSeleccionados = false;
                    break;
                }
            }

            if (todosDestinatariosSeleccionados) {
                resultado.add(new ChipDto(-2, "Todos los destinatarios", "todosDestinatarios"));
                for (DestinatarioDto d : destinatariosLista) destinatariosMarcados.add(d.getId()); // para filtrar alumnos
            } else {
                for (DestinatarioDto d : destinatariosLista) {
                    if (d.isSeleccionado()) {
                        resultado.add(new ChipDto(d.getId(), d.getNombre(), "destinatario"));
                        destinatariosMarcados.add(d.getId());
                    }
                }
            }
        }
        chips.setValue(resultado); // actualizo chips visibles en UI
    }


    /**
     * Cierra un chip individual marcando su entidad como no seleccionada.
     * Actualmente soporta tipo "personal".
     * @param id ID del destinatario.
     * @param tipo Tipo del chip (ej. "personal").
     */
    public void cerrarChip(int id, String tipo) {
        if (tipo.equals("personal")) {
            List<DestinatarioDto> lista = getDestinatarios().getValue();
            if (lista != null) {
                for (DestinatarioDto d : lista) {
                    if (d.getId() == id) {
                        d.setSeleccionado(false);
                        break;
                    }
                }
                destinatarios.setValue(new ArrayList<>(lista)); //  forza LiveData
            }

        }
        sincronizarChips(); //  actualizo chips en UI
    }

    /**
     * Desmarca todos los destinatarios seleccionados y actualiza la UI.
     */
    public void limpiarDestinatarios() {
        List<DestinatarioDto> lista = getDestinatarios().getValue();
        if (lista != null) {
            for (DestinatarioDto d : lista) {
                d.setSeleccionado(false);
            }
            destinatarios.setValue(new ArrayList<>(lista));
        }

        sincronizarChips(); // actualizar UI
    }

}