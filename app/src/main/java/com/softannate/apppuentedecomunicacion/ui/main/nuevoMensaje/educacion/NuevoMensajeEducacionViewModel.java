package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.educacion;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.base.ViewModelConversacionNuevo;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.ChipDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel encargado de gestionar el flujo de creación de mensajes educativos.
 * Extiende {@link ViewModelConversacionNuevo} para aprovechar su estructura base.
 * Controla la carga y cacheo de cursos, alumnos y destinatarios, y expone datos
 * para ser usados en la UI mediante LiveData.
 */
public class NuevoMensajeEducacionViewModel extends ViewModelConversacionNuevo {

    //Esto guarda y expone los cursos una sola vez cuando tocás el radio “Alumnos”
    private final MutableLiveData<List<AlumnoDto>> alumnos = new MutableLiveData<>();

    /**
     * Devuelve el curso correspndiente al Id indicado
     * @param id Id del curso
     * @return CursoDto asociado o null si no está en cache
     */
    /*
    public CursoDto getCursoPorId(int id) {
        return cacheCursosPorId.get(id);
    }
     */

/*
    public DestinatarioDto getPersonalPorId(int id) {
        return cachePersonalPorId.get(id);
    }

 */


    //-----------------------------------------Cache de Datos----    --------------------------------------

    /**Cache local de cursos por su Id*/
    private final Map<Integer, CursoDto> cacheCursosPorId = new HashMap<>();

    /**Cache local de destintarios por su Id*/
    private final Map<Integer, DestinatarioDto> cachePersonalPorId = new HashMap<>();

    /**
     * Devuelve el mapa completo de cursos por Id
     * @return Mapa de cursos
     */
    public Map<Integer, CursoDto> getCursosPorId() {
        return cacheCursosPorId;
    }

    /**
     * Devuelve el mapa completo de personal educativo cacheado por ID.
     * @return Mapa de destinatarios
     */
    public Map<Integer, DestinatarioDto> getPersonalPorId() {
        return cachePersonalPorId;
    }

    //-------------------------------------------Constructor--------------------------------------------
    public NuevoMensajeEducacionViewModel(@NonNull Application application) {
        super(application);
        obtenerCategorias();
    }

    //-------------------------------------------LiveData UI----------------------------------------------

    /** LiveData que contiene la lista de chips activos en la UI. */
    private final MutableLiveData<List<ChipDto>> chips = new MutableLiveData<>(new ArrayList<>());

    // IDs de alumnos seleccionados individualmente
    private final MutableLiveData<Set<Integer>> alumnosSeleccionados = new MutableLiveData<>(new HashSet<>());

    //para mostrar alumnos al usuario
    /**lista que uso en el fragment para llenar el RecyclerView de alumnos al abrír un curso.(Recycler del curso expandido)*/
    private final MutableLiveData<List<AlumnoDto>> alumnosVisibles = new MutableLiveData<>();

    /**Id del curso actualmente expandido en la interfaz*/
    private final MutableLiveData<Integer> cursoExpandido = new MutableLiveData<>();

    /**
     * Devuelve la lista observable de destinatarios
     * @return LiveData con la lista de destinatarios
     */
    public LiveData<List<DestinatarioDto>> getDestinatarios() {
        return destinatarios;
    }

      /*
    public LiveData<Set<Integer>> getAlumnosSeleccionados() {
        return alumnosSeleccionados;
    }

 */

    /**
     * Devuelve la lista observable de alumnos
     * @return LiveData con la lista de alumnos mostrados
     */
    public LiveData<List<AlumnoDto>> getAlumnosVisibles() {
        return alumnosVisibles;
    }

    //------------------------------------------Metodos Api--------------------------------------------

    /**
     * Realiza la solicitud a la API para obtener los destinatarios educativos.
     * Cachea los resultados y actualiza el LiveData correspondiente.
     * Si ya están cacheados, evita la llamada.
     */
    public void obtenerDestinatarios() {
        if (!cachePersonalPorId.isEmpty()) return;

        Call<List<DestinatarioDto>> call = endpoints.obtenerDestinatariosEducativos();
        call.enqueue(new Callback<List<DestinatarioDto>>() {
            @Override
            public void onResponse(Call<List<DestinatarioDto>> call, Response<List<DestinatarioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DestinatarioDto> lista = response.body();
                    destinatarios.setValue(lista);
                    for (DestinatarioDto d : lista) cachePersonalPorId.put(d.getId(), d);

                    Log.d("ViewModelResponse333", "Destinatarios cargados correctamente: " + response.body());
                } else {
                    Log.d("ViewModelResponseError", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DestinatarioDto>> call, Throwable throwable) {
                Log.d("ViewModelResponseConnectionError", "Error de conexión: " + throwable.getMessage());
            }
        });
    }

    /**
     * Realiza la solicitud a la API para obtener los cursos disponibles.
     * Cachea los resultados y actualiza el LiveData correspondiente.
     * Si ya están cacheados, evita la llamada.
     */
    public void cargarCursos() {
        if (!cacheCursosPorId.isEmpty()) {
            cursos.setValue(new ArrayList<>(cacheCursosPorId.values()));
            return;
        }
        Call<List<CursoDto>> call = endpoints.obtenerCursos();
        call.enqueue(new Callback<List<CursoDto>>() {
            @Override
            public void onResponse(Call<List<CursoDto>> call, Response<List<CursoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CursoDto> lista = response.body();
                    cursos.setValue(lista);

                    for (CursoDto c : lista) cacheCursosPorId.put(c.getId(), c);



                    Log.d("ViewModelNuevo", "Cursos cargados correctamente: " + response.body());
                } else {
                    Log.d("ViewModelNuevo", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CursoDto>> call, Throwable throwable) {
                Log.d("ViewModelNuevo", "Error de conexión: " + throwable.getMessage());
            }
        });
    }

    /**
     * Realiza la solicitud a la API para obtener los alumnos de un curso específico.
     * Si ya están cacheados, actualiza la UI directamente. Marca automáticamente
     * alumnos si el curso está previamente seleccionado.
     *
     * @param cursoId ID del curso del cual se desea obtener los alumnos
     */
    public void cargarAlumnosDeCurso(int cursoId) {
        cursoExpandido.setValue(cursoId);

        // Ya está cacheado → lo mostramos directamente
        if (cacheAlumnosPorCurso.containsKey(cursoId)) {
            List<AlumnoDto> lista = cacheAlumnosPorCurso.get(cursoId);

            // si el curso está marcado, marco los alumnos
            CursoDto curso = getCursos().getValue().stream()
                    .filter(c -> c.getId() == cursoId)
                    .findFirst()
                    .orElse(null);

            if (curso != null && curso.isSeleccionado()) {
                for (AlumnoDto a : lista) a.setSeleccionado(true);
            }

            alumnosVisibles.setValue(new ArrayList<>(lista));
            return;
        }

        Call<List<AlumnoDto>> call = endpoints.obtenerAlumnosDeCurso(cursoId);
        call.enqueue(new Callback<List<AlumnoDto>>() {
            @Override
            public void onResponse(Call<List<AlumnoDto>> call, Response<List<AlumnoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlumnoDto> lista = response.body();
                    Log.d("ViewModelNuevo", "Alumnos cargados correctamente: " + response.body());
                    cacheAlumnosPorCurso.put(cursoId, lista); // cacheamos
                    // Si el curso está marcado, marco los alumnos recién traídos
                    CursoDto curso = getCursos().getValue().stream()
                            .filter(c -> c.getId() == cursoId)
                            .findFirst()
                            .orElse(null);

                    if (curso != null && curso.isSeleccionado()) {
                        for (AlumnoDto a : lista) a.setSeleccionado(true);
                    }
                    alumnosVisibles.setValue(lista);           // mostramos
                } else {
                    alumnosVisibles.setValue(Collections.emptyList());
                }
            }
            @Override
            public void onFailure(Call<List<AlumnoDto>> call, Throwable throwable) {
                alumnosVisibles.setValue(Collections.emptyList());
            }
        });
    }


    //------------------------------------------Lógica Auxiliar--------------------------------------
    /**
     * Devuelve los alumnos cacheados para un curso dado.
     * @param cursoId ID del curso
     * @return Lista de alumnos asociados al curso, o null si no está cacheado
     */
    public List<AlumnoDto> getAlumnosDeCurso(int cursoId) {
        return cacheAlumnosPorCurso.getOrDefault(cursoId, null);
    }

    /**
     * Devuelve el mapa completo de alumnos agrupados por curso.
     * @return Mapa curso → lista de alumnos
     */
    public Map<Integer, List<AlumnoDto>> getAlumnosPorCurso() {
        return cacheAlumnosPorCurso;
    }

    /**
     * Devuelve la lista observable de cursos disponibles.
     * @return LiveData con los cursos cargados.
     */
    public LiveData<List<CursoDto>> getCursos() {
        return cursos;
    }

    /**
     * Devuelve el ID del curso actualmente expandido en la UI.
     * @return ID del curso expandido, o -1 si no hay ninguno.
     */
    public int getCursoExpandidoId() {
        return cursoExpandido.getValue() != null ? cursoExpandido.getValue() : -1;
    }

    /**
     * Establece el ID del curso actualmente expandido.
     * @param id ID del curso que debe expandirse.
     */
    public void setCursoExpandido(int id) {
        cursoExpandido.setValue(id);
    }


    /**
     * Devuelve el LiveData observable de chips, usado para representación visual.
     * @return lista de chips actualizados.
     */
    public LiveData<List<ChipDto>> getChips() {
        return chips;
    }


//------------------------------------------Chips--------------------------------------


    /**
     * Devuelve los alumnos seleccionados del curso actualmente expandido.
     * Ideal para mostrar chips específicos de alumnos individuales.
     * @return Lista de alumnos marcados como seleccionados.
     */
    public List<AlumnoDto> getAlumnosSeleccionadosExpandidos() {
        int idCurso = getCursoExpandidoId();
        List<AlumnoDto> lista = cacheAlumnosPorCurso.get(idCurso);
        if (lista == null) return new ArrayList<>();
        return lista.stream()
                .filter(AlumnoDto::isSeleccionado)
                .collect(Collectors.toList());
    }


    /**
     * Reconstruye la lista de chips con base en el estado actual de cursos, alumnos y destinatarios.
     * Los chips incluyen:
     * - Todos los cursos
     * - Cursos individuales
     * - Alumnos seleccionados no vinculados a cursos seleccionados
     * - Todo el personal
     * - Personal individual
     */
    public void sincronizarChips() {
        List<ChipDto> resultado = new ArrayList<>();

        //  CURSOS
        List<CursoDto> cursosLista = getCursos().getValue();
        Set<Integer> cursosMarcados = new HashSet<>();

        if (cursosLista != null && !cursosLista.isEmpty()) {
            boolean todosCursosSeleccionados = true;
            for (CursoDto c : cursosLista) {
                if (!c.isSeleccionado()) {
                    todosCursosSeleccionados = false;
                    break;
                }
            }

            if (todosCursosSeleccionados) {
                resultado.add(new ChipDto(-2, "Todos los cursos", "todosCursos"));
                for (CursoDto c : cursosLista) cursosMarcados.add(c.getId()); // para filtrar alumnos
            } else {
                for (CursoDto c : cursosLista) {
                    if (c.isSeleccionado()) {
                        resultado.add(new ChipDto(c.getId(), c.getNombre(), "curso"));
                        cursosMarcados.add(c.getId());
                    }
                }
            }
        }

        //  ALUMNOS
        for (Map.Entry<Integer, List<AlumnoDto>> entry : cacheAlumnosPorCurso.entrySet()) {
            int cursoId = entry.getKey();
            if (cursosMarcados.contains(cursoId)) continue; // omitimos si curso está marcado

            List<AlumnoDto> alumnos = entry.getValue();
            if (alumnos != null) {
                for (AlumnoDto a : alumnos) {
                    if (a.isSeleccionado()) {
                        resultado.add(new ChipDto(a.getId(), a.getNombre(), "alumno"));
                    }
                }
            }
        }

        //  PERSONAL
        List<DestinatarioDto> personal = getDestinatarios().getValue();
        if (personal != null && !personal.isEmpty()) {
            boolean todoPersonalSeleccionado = true;
            for (DestinatarioDto d : personal) {
                if (!d.isSeleccionado()) {
                    todoPersonalSeleccionado = false;
                    break;
                }
            }

            if (todoPersonalSeleccionado) {
                resultado.add(new ChipDto(-1, "Todo el personal", "establecimiento"));
            } else {
                for (DestinatarioDto d : personal) {
                    if (d.isSeleccionado()) {
                        resultado.add(new ChipDto(d.getId(), d.getNombre(), "personal"));
                    }
                }
            }
        }

        chips.setValue(resultado); //  actualiza chips visibles en UI
    }

    /**
     * Elimina el chip de la UI y desmarca su estado según el tipo asociado.
     * Tipos admitidos: curso, alumno, personal, establecimiento, todosCursos.
     *
     * @param id ID del chip (o valor especial como -1 o -2).
     * @param tipo Tipo del chip que se desea cerrar.
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

        } else if (tipo.equals("curso")) {
            List<CursoDto> lista = getCursos().getValue();
            if (lista != null) {
                for (CursoDto c : lista) {
                    if (c.getId() == id) {
                        c.setSeleccionado(false);
                        break;
                    }
                }
                cursos.setValue(new ArrayList<>(lista)); //  actualiza cursos

                //  Desmarcar alumnos de ese curso también
                List<AlumnoDto> alumnos = cacheAlumnosPorCurso.get(id);
                if (alumnos != null) {
                    for (AlumnoDto a : alumnos) a.setSeleccionado(false);

                    //  si ese curso está expandido, actualizar alumnos visibles también
                    Integer cursoExp = cursoExpandido.getValue();
                    if (cursoExp != null && cursoExp == id) {
                        alumnosVisibles.setValue(new ArrayList<>(alumnos));
                    }
                }
            }

        } else if (tipo.equals("alumno")) {
            List<AlumnoDto> lista = alumnosVisibles.getValue();
            if (lista != null) {
                for (AlumnoDto a : lista) {
                    if (a.getId() == id) {
                        a.setSeleccionado(false);
                        break;
                    }
                }
                alumnosVisibles.setValue(new ArrayList<>(lista)); //  forza LiveData
            }

        } else if (tipo.equals("establecimiento") && id == -1) {
            List<DestinatarioDto> lista = getDestinatarios().getValue();
            if (lista != null) {
                for (DestinatarioDto d : lista) d.setSeleccionado(false);
                destinatarios.setValue(new ArrayList<>(lista));
            }

        } else if (tipo.equals("todosCursos") && id == -2) {
            //  Desmarca todos los cursos
            List<CursoDto> cursosLista = getCursos().getValue();
            if (cursosLista != null) {
                for (CursoDto c : cursosLista) c.setSeleccionado(false);
                cursos.setValue(new ArrayList<>(cursosLista));
            }

            //  Desmarca todos los alumnos relacionados
            for (List<AlumnoDto> alumnos : cacheAlumnosPorCurso.values()) {
                if (alumnos != null) {
                    for (AlumnoDto a : alumnos) a.setSeleccionado(false);
                }
            }

            //  Actualizar alumnosVisibles si está expandido
            Integer cursoExp = cursoExpandido.getValue();
            if (cursoExp != null) {
                List<AlumnoDto> visibles = cacheAlumnosPorCurso.get(cursoExp);
                if (visibles != null) alumnosVisibles.setValue(new ArrayList<>(visibles));
                else alumnosVisibles.setValue(Collections.emptyList());
            }
        }

        sincronizarChips(); //  actualiza chips en UI
    }
}