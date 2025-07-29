package com.softannate.apppuentedecomunicacion.ui.main.agenda;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoConAlumnosDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoDto;
import com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.educacion.NuevoMensajeEducacionViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para la agenda educativa. Gestiona los cursos, alumnos y operaciones de búsqueda y contacto.
 */
public class AgendaViewModel extends NuevoMensajeEducacionViewModel {


    // LiveData privados
    private final MutableLiveData<CursoConAlumnosDto> cursoConAlumnos = new MutableLiveData<>();
    private final MutableLiveData<List<AlumnoDto>> resultadoBusqueda = new MutableLiveData<>();
    private final MutableLiveData<Boolean> llamar = new MutableLiveData<>();

    // Otros campos
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private AlumnoDto alumnoActual;


    /**
     * Constructor que inicializa el ViewModel con contexto de aplicación.
     */
    public AgendaViewModel(@NonNull Application application) {
        super(application);
    }

    // Campo privado con lógica de transformación
    /**
     * Contiene los nombres extraídos del LiveData de cursos.
     * Derivado mediante Transformations.map para separar solo los nombres.
     */
    private final LiveData<List<String>> nombresCursos = Transformations.map(cursos, lista -> {
        // lista.stream().map(CursoDto::getNombre).collect(Collectors.toList())
        List<String> nombres = new ArrayList<>();
        if (lista != null) {
            for (CursoDto curso : lista) {
                nombres.add(curso.getNombre());
            }
        }
        return nombres;
    });

    // Método público con contexto completo para el consumidor
    /**
     * Devuelve un LiveData observable con los nombres de los cursos disponibles.
     * <p>
     * Útil para mostrar en listas, búsquedas o selección de cursos por nombre.
     *
     * @return LiveData con nombres de cursos como String.
     */
    public LiveData<List<String>> getNombresCursos() {
        return nombresCursos;
    }

    /**
     * Devuelve los datos del curso seleccionado junto a sus alumnos.
     */
    public LiveData<CursoConAlumnosDto> getCursoConAlumnos() {
        return cursoConAlumnos;
    }

    /**
     * Indica si se debe iniciar una llamada al alumno seleccionado.
     */
    public LiveData<Boolean> getLlamar() {
        return llamar;
    }
    /**
     * Devuelve los resultados de la búsqueda de alumnos.
     */
    public LiveData<List<AlumnoDto>> getResultadoBusqueda() {
        return resultadoBusqueda;
    }


    // ------------------------------------------ CURSOS -------------------------------------------

    /**
     * Filtra texto de búsqueda o recarga los cursos si el texto está vacío.
     */
    public void textoBusqueda(String nuevoTexto) {
        if (nuevoTexto == null || nuevoTexto.trim().isEmpty()) {
            cargarCursos();
        } else {
            realizarBusquedaConDemora(nuevoTexto);
        }
    }



    // ---------------------------------- SELECCIÓN DE CURSO -----------------------------------

    /**
     * Busca el curso por nombre y carga sus alumnos. Emite el resultado como LiveData.
     * @param nombreCurso nombre del curso a seleccionar
     */
    public void seleccionarCurso(String nombreCurso) {
        Map<Integer, CursoDto> cursosPorId = getCursosPorId();
        CursoDto cursoSeleccionado = null;
        for (CursoDto c : cursosPorId.values()) {
            if (nombreCurso.equals(c.getNombre())) {
                cursoSeleccionado = c;
                break;
            }
        }

        if (cursoSeleccionado != null) {
            int cursoId = cursoSeleccionado.getId();
            cargarAlumnosDeCurso(cursoId);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                List<AlumnoDto> alumnos = getAlumnosDeCurso(cursoId);
                if (alumnos != null) {
                    for (AlumnoDto a : alumnos) {
                        a.setSeleccionado(false);
                    }
                    cursoConAlumnos.setValue(new CursoConAlumnosDto(nombreCurso, alumnos));
                }
            }, 150);
        }
    }


    // --------------------------------- ALUMNO Y TELÉFONO -------------------------------------

    /**
     * Devuelve el alumno actualmente seleccionado.
     */
    public AlumnoDto getAlumnoActual() {
        return alumnoActual;
    }

    /**
     * Evalúa si el alumno tiene teléfono disponible. Si lo tiene, activa el LiveData para llamar.
     * @param alumno alumno a evaluar
     */
    public void evaluarTelefono(AlumnoDto alumno) {
        this.alumnoActual = alumno;
        List<String> telefonos = alumno.getTelefonos();
        if (telefonos != null && !telefonos.isEmpty()) {
            llamar.setValue(true);
        } else {
            mensaje.setValue("El alumno seleccionado no tiene teléfono agendado.");
        }
    }


    // ----------------------------------- BÚSQUEDA DE ALUMNOS -------------------------------------

    /**
     * Ejecuta una búsqueda con texto, incluyendo retardo para evitar múltiples solicitudes.
     * @param texto texto ingresado por el usuario
     */
    public void realizarBusquedaConDemora(String texto) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = () -> {
            if (texto != null && texto.trim().length() >= 2) {
                buscarAlumnos(texto.trim());
            } else {
                Log.d("AgendaViewModel", "Texto demasiado corto para buscar");
            }
        };
        handler.postDelayed(runnable, 500); // espera 500ms
    }

    /**
     * Realiza la solicitud al backend para buscar alumnos por texto.
     * @param texto texto de búsqueda
     */
    public void buscarAlumnos(String texto) {
        Log.d("AgendaViewModel", "Buscando alumnos con texto: '" + texto + "'");

        Call<List<AlumnoDto>> call=endpoints.buscarAlumnos(texto);
        call.enqueue(new Callback<List<AlumnoDto>>() {
            @Override
            public void onResponse(Call<List<AlumnoDto>> call, Response<List<AlumnoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlumnoDto> alumnos = response.body();
                    resultadoBusqueda.setValue(response.body());
                    Log.d("AgendaViewModel", "Alumnos cargados correctamente: " + response.body());
                } else {
                    mensaje.setValue("Error al buscar alumnos");
                    Log.d("AgendaViewModel", "Error cargando alumnos: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<AlumnoDto>> call, Throwable t) {
                Log.d("AgendaViewModel", "Error cargando alumnos: " + t.getMessage());
            }
        });
    }
}