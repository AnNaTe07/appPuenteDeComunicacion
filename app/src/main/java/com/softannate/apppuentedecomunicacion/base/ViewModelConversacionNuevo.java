package com.softannate.apppuentedecomunicacion.base;


import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.softannate.apppuentedecomunicacion.modelos.Categoria_Mensaje;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeEducativoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeTutorDto;
import com.softannate.apppuentedecomunicacion.utils.ArchivoAdjuntoHelper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel encargado de gestionar el proceso de creación y envío de mensajes.
 * Maneja la selección de cursos, alumnos, destinatarios y categorías.
 * Se comunica con el backend mediante llamadas Retrofit.
 */
public class ViewModelConversacionNuevo extends ViewModelBase{

    protected final MutableLiveData<List<CursoDto>> cursos = new MutableLiveData<>();
    protected MutableLiveData<List<DestinatarioDto>> destinatarios = new MutableLiveData<>();
    private MutableLiveData<List<Categoria_Mensaje>> listaCategorias = new MutableLiveData<>();

    /** Map que almacena alumnos agrupados por curso. */
    protected final Map<Integer, List<AlumnoDto>> cacheAlumnosPorCurso = new HashMap<>();

    public ViewModelConversacionNuevo(@NonNull Application application) {
        super(application);
    }

    /**
     * Devuelve la lista observable de categorías de mensajes.
     * Se actualiza mediante obtenerCategorias().
     * @return LiveData con la lista de categorías disponibles.
     */
    public LiveData<List<Categoria_Mensaje>> getCategorias() {
        return listaCategorias;
    }

    /**
     * Devuelve la lista de destinatarios seleccionados por el usuario.
     * @return Lista de DestinatarioDto marcados como seleccionados.
     */
    public List<DestinatarioDto> getPersonalSeleccionado() {
        List<DestinatarioDto> lista = destinatarios.getValue();
        if (lista == null) return new ArrayList<>();
        return lista.stream()
                .filter(DestinatarioDto::isSeleccionado)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve la lista de cursos seleccionados por el usuario.
     * @return Lista de CursoDto marcados como seleccionados.
     */
    public List<CursoDto> getCursosSeleccionados() {
        List<CursoDto> lista = cursos.getValue();
        if (lista == null) return new ArrayList<>();
        return lista.stream()
                .filter(CursoDto::isSeleccionado)
                .collect(Collectors.toList());
    }

    //-------------------------------------LOGICA DE NEGOCIO--------------------------------------
    /**
     * Construye el DTO de mensaje educativo a partir del estado actual del ViewModel.
     * Incluye texto, categoría, cursos, alumnos y destinatarios seleccionados.
     *
     * @param texto Texto del mensaje.
     * @param idCategoria ID de la categoría seleccionada.
     * @param enviarATodoEstablecimiento True si debe enviarse a todo el establecimiento.
     * @return DTO del mensaje educativo listo para enviar.
     */
    public MensajeEducativoDto construirPayload(String texto, int idCategoria, boolean enviarATodoEstablecimiento) {
        MensajeEducativoDto dto = new MensajeEducativoDto();
        dto.setMensaje(texto);
        dto.setIdCategoria(idCategoria);
        dto.setEstablecimiento(enviarATodoEstablecimiento);

        List<Integer> idCursos = getCursosSeleccionados().stream()
                .map(CursoDto::getId)
                .collect(Collectors.toList());

        List<Integer> idAlumnos = new ArrayList<>();
        Set<Integer> cursosMarcados = new HashSet<>(idCursos);
        for (Map.Entry<Integer, List<AlumnoDto>> entry : cacheAlumnosPorCurso.entrySet()) {
            if (cursosMarcados.contains(entry.getKey())) continue;
            for (AlumnoDto a : entry.getValue()) {
                if (a.isSeleccionado()) idAlumnos.add(a.getId());
            }
        }

        dto.setIdCursos(idCursos);
        dto.setIdAlumnos(idAlumnos);

        if (!enviarATodoEstablecimiento) {
            List<Integer> idUsuarios = getPersonalSeleccionado().stream()
                    .map(DestinatarioDto::getId)
                    .collect(Collectors.toList());
            dto.setIdUsuarios(idUsuarios);
        } else {
            dto.setIdUsuarios(Collections.emptyList()); // 🔒 explícitamente vacío
        }

        return dto;
    }


    /**
     * Envia el mensaje educativo al servidor usando Retrofit.
     * Valida el contenido del mensaje y la selección de destinatarios antes del envío.
     *
     * @param context Contexto de la aplicación (necesario para adjuntos).
     * @param mensajeRequest DTO del mensaje educativo.
     */
    public void enviarMensajeEducativo(Context context,MensajeEducativoDto mensajeRequest) {
        Log.d("mensajeRequest", "MensajeRequest: " + mensajeRequest);
        if (mensajeRequest.getMensaje() == null || mensajeRequest.getMensaje().trim().isEmpty()) {
            Log.e("NuevoMensaje", "El mensaje está vacío");
            mensaje.setValue("Debe escribir un mensaje");
            return;
        }

        if (mensajeRequest.getIdCategoria() == 0) {
            Log.e("NuevoMensaje", "La etiqueta no está seleccionada");
            mensaje.setValue("Debe seleccionar una categoría");
            return;
        }
        if(mensajeRequest.getIdAlumnos().isEmpty() && mensajeRequest.getIdCursos().isEmpty() && mensajeRequest.getIdUsuarios().isEmpty() && mensajeRequest.isEstablecimiento() == false) {
            mensaje.setValue("Debe seleccionar un destinatario");
            return;
        }

        enviandoMensaje.setValue(true);
        // Convertís el DTO a JSON
        String json = new Gson().toJson(mensajeRequest);
        RequestBody cuerpoDto = RequestBody.create(json, MediaType.parse("application/json"));

        // Preparás los archivos adjuntos
        List<MultipartBody.Part> partes = prepararAdjuntos(context);

        // Enviás TODO con Retrofit
        Call<Void> call = endpoints.enviarMensajeEducativo(cuerpoDto, partes);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MensajeEducacion", "Mensaje enviado correctamente");
                    mensaje.setValue("Mensaje enviado correctamente");
                    limpiar.setValue(true);
                    enviandoMensaje.setValue(false);
                    limpiarArchivos();
                } else {
                    mensaje.setValue("Error al enviar mensaje");
                    Log.e("MensajeEducacion", "Error al enviar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MensajeEducacion", "Falló el envío: " + t.getMessage());
            }
        });
    }


    //------------------------------METODOS AUXILIARES / MUTACIONES DE DATOS----------------------


    /**
     * Agrega un alumno directo al mapa de cache, con curso ficticio ID 0.
     * @param idAlumno ID del alumno a agregar como único seleccionado.
     */
    public void setAlumnoDirecto(int idAlumno) {
        AlumnoDto a = new AlumnoDto();
        a.setId(idAlumno);
        a.setSeleccionado(true);
        limpiarAlumnos();
        cacheAlumnosPorCurso.put(0, List.of(a));
    }

    /**
     * Establece la lista de destinatarios seleccionados por el usuario.
     * @param lista Lista de DestinatarioDto marcados desde la UI.
     */
    public void setDestinatariosSeleccionados(List<DestinatarioDto> lista) {
        destinatarios.setValue(lista);
    }

    /**
     * Limpia completamente el mapa de alumnos por curso.
     */
    public void limpiarAlumnos() {
        cacheAlumnosPorCurso.clear();
    }

    /**
     * Realiza una llamada al backend para obtener la lista de categorías de mensaje.
     * El resultado se almacena en listaCategorias.
     */
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
                    Log.e("ListaNombresViewModel", "Error en la llamada al obtener categorias: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Categoria_Mensaje>> call, Throwable throwable) {
                Log.e("ListaNombresViewModel", "Error en la llamada : " + throwable.getMessage());
            }
        });
    }


    //-----------------------------------METODOS PARA TUTOR---------------------------------------
    /**
     * Mapa para almacenar los destinatarios disponibles por alumno (cache local).
     */
    private final Map<Integer, List<DestinatarioDto>> usuariosPorAlumno = new HashMap<>();

    /**
     * Envía un mensaje educativo desde un tutor hacia destinatarios seleccionados.
     * Realiza validaciones previas y prepara los archivos adjuntos.
     *
     * @param context Contexto necesario para convertir los archivos
     * @param mensajeRequest DTO que contiene el mensaje a enviar
     */
    public void enviarMensajeTutor(Context context,MensajeTutorDto mensajeRequest) {
        Log.d("mensajeRequest", "MensajeRequest: " + mensajeRequest);
        if (mensajeRequest.getMensaje() == null || mensajeRequest.getMensaje().trim().isEmpty()) {
            Log.e("NuevoMensaje", "El mensaje está vacío");
            mensaje.setValue("Debe escribir un mensaje");
            return;
        }

        if (mensajeRequest.getIdCategoria() == 0) {
            Log.e("NuevoMensaje", "La etiqueta no está seleccionada");
            mensaje.setValue("Debe seleccionar una categoría");
            return;
        }
        if(mensajeRequest.getIdUsuarios().isEmpty()) {
            mensaje.setValue("Debe seleccionar un destinatario");
            return;
        }

        enviandoMensaje.setValue(true);
        // Convertís el DTO a JSON
        String json = new Gson().toJson(mensajeRequest);
        RequestBody cuerpoDto = RequestBody.create(json, MediaType.parse("application/json"));

        // Preparás los archivos adjuntos
        List<MultipartBody.Part> partes = prepararAdjuntos(context);

        // Envío TODO con Retrofit
        Call<Void> call = endpoints.enviarMensajeTutor(cuerpoDto, partes);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MensajeEducacion", "Mensaje enviado correctamente");
                    mensaje.setValue("Mensaje enviado correctamente");
                    limpiar.setValue(true);
                    enviandoMensaje.setValue(false);
                    limpiarArchivos();
                } else {
                    mensaje.setValue("Error al enviar: " + response.message());
                    Log.e("MensajeEducacion", "Error al enviar: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MensajeEducacion", "Falló el envío: " + t.getMessage());
            }
        });
    }

    /**
     * Carga los destinatarios posibles para un alumno determinado.
     * Utiliza cache local si está disponible, o accede a la API para obtenerlos.
     *
     * @param idAlumno ID del alumno cuyos tutores se desean cargar
     */
    public void cargarUsuariosPorAlumno(int idAlumno) {
        //Si ya está en el cache
        if (usuariosPorAlumno.containsKey(idAlumno)) {
            destinatarios.setValue(usuariosPorAlumno.get(idAlumno));
            Log.d("ViewModelNuevoTutor", "Usuarios cargados desde cache para alumno: " + idAlumno);
            return;
        }

        Call<List<DestinatarioDto>> call = endpoints.destinatariosPorAlumno(idAlumno);
        call.enqueue(new Callback<List<DestinatarioDto>>() {
            @Override
            public void onResponse(Call<List<DestinatarioDto>> call, Response<List<DestinatarioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuariosPorAlumno.put(idAlumno, response.body()); // Guardo en cache
                   //usuariosSeleccionados.setValue(response.body());
                    destinatarios.setValue(response.body());
                    Log.d("ViewModelNuevoTutor", "Usuarios cargados desde API para alumno: " + idAlumno);
                    Log.d("ViewModelNuevoTutor", "Usuarios" + response.body());
                } else {
                    Log.e("ViewModelNuevoTutor", "Error de carga usuarios alumno " + idAlumno + ": " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<DestinatarioDto>> call, Throwable t) {
                Log.e("ViewModelNuevoTutor", "Error de conexión al cargar usuarios de alumno " + idAlumno + ": " + t.getMessage());
            }
        });
    }

    /**
     * Construye el DTO de mensaje educativo para tutores, a partir de los datos actuales del ViewModel.
     *
     * @param texto Contenido del mensaje
     * @param idCategoria ID de la categoría elegida
     * @param idAlumno ID del alumno al que se asocia el mensaje
     * @return DTO del mensaje educativo para tutor
     */
    public MensajeTutorDto construirPayloadTutor(String texto, int idCategoria, int idAlumno) {

        MensajeTutorDto dto = new MensajeTutorDto();
        dto.setMensaje(texto);
        dto.setIdCategoria(idCategoria);
        dto.setIdAlumno(idAlumno);

        List<Integer> idUsuariosSeleccionados = getPersonalSeleccionado().stream()

                .map(DestinatarioDto::getId)
                .collect(Collectors.toList());

      Log.d("UsuariosSeleccionados", "Usuarios seleccionados: " + idUsuariosSeleccionados);
        dto.setIdUsuarios(idUsuariosSeleccionados);


        return dto;
    }

    //----------------------------------------ARCHIVOS ADJUNTOS------------------------------------

    /** Límite máximo de archivos adjuntos permitidos por mensaje. */
    private static final int MAX_FILES = 3;

    /** LiveData que contiene los archivos seleccionados por el usuario. */
    private final MutableLiveData<List<Uri>> _archivos = new MutableLiveData<>(new ArrayList<>());

    /** Vista pública de los archivos seleccionados. */
    public LiveData<List<Uri>> archivos = _archivos;

    /**
     * Intenta agregar una lista de URIs como archivos adjuntos.
     * Verifica el peso y el tipo permitido, y respeta el límite máximo.
     *
     * @param context Contexto necesario para validar y leer archivos
     * @param nuevos Lista de URIs seleccionados por el usuario
     */
    public void agregarArchivos(Context context, List<Uri> nuevos) {
        List<Uri> actuales = new ArrayList<>(_archivos.getValue());
        int espacio = MAX_FILES - actuales.size();

        if (espacio <= 0) {
            mensaje.setValue("Ya tenés 3 archivos adjuntos. Eliminá alguno para agregar más.");
            return;
        }

        List<Uri> válidos = new ArrayList<>();
        for (Uri uri : nuevos) {
            if (ArchivoAdjuntoHelper.esArchivoValido(context, uri)) {
                válidos.add(uri);
            } else {
                String nombre = ArchivoAdjuntoHelper.obtenerNombre(context, uri);
                mensaje.setValue("Archivo demasiado pesado: " + nombre);
            }
        }

        if (válidos.size() > espacio) {
            mensaje.setValue("Solo se pueden añadir 3 archivos. Se cargarán los primeros " + espacio + ".");
            válidos = válidos.subList(0, espacio);
        }

        actuales.addAll(válidos);
        _archivos.setValue(actuales);
    }

    /**
     * Elimina un archivo previamente seleccionado.
     *
     * @param uri URI del archivo que se desea quitar
     */
    public void eliminarArchivo(Uri uri) {
        List<Uri> actuales = new ArrayList<>(_archivos.getValue());
        actuales.removeIf(u -> u.equals(uri));
        _archivos.setValue(actuales);
    }

    /**
     * Prepara los archivos adjuntos en formato Multipart para enviar por Retrofit.
     *
     * @param context Contexto necesario para acceder al contenido de los archivos
     * @return Lista de partes Multipart listas para enviar
     */
    public List<MultipartBody.Part> prepararAdjuntos(Context context) {
        List<MultipartBody.Part> partes = new ArrayList<>();
        List<Uri> uris = archivos.getValue();

        Log.d("Adjuntos", "Cantidad de URIs: " + (uris != null ? uris.size() : 0));

        if (uris == null || uris.isEmpty()) {
            Log.w("Adjuntos", " No hay archivos seleccionados");
            return partes;
        }

        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            Log.d("Adjuntos", "Procesando archivo: " + uri.toString());

            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = inputStream.read(data)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                byte[] bytes = buffer.toByteArray();

                String nombre = ArchivoAdjuntoHelper.obtenerNombre(context, uri);
                //RequestBody cuerpo = RequestBody.create(bytes, MediaType.parse("application/octet-stream"));

                String mimeType = context.getContentResolver().getType(uri);
                if (mimeType == null) mimeType = "application/octet-stream"; // fallback

                RequestBody cuerpo = RequestBody.create(bytes, MediaType.parse(mimeType));

                MultipartBody.Part parte = MultipartBody.Part.createFormData("archivos", nombre, cuerpo);
                partes.add(parte);

                Log.d("Adjuntos", "Archivo agregado: " + nombre + ", tamaño: " + bytes.length);
            } catch (Exception e) {
                Log.e("Adjuntos", "Error al convertir archivo: " + e.getMessage());
                mensaje.setValue("Error al convertir archivo: " + e.getMessage());
            }
        }

        Log.d("Adjuntos", "Total partes creadas: " + partes.size());
        return partes;
    }

    /**
     * Limpia completamente la lista de archivos adjuntos seleccionados.
     */
    public void limpiarArchivos() {
        _archivos.setValue(new ArrayList<>());
    }

//----------------------------------------------------------------------------------------

    /** LiveData que indica el índice de mensaje actualizado en la conversación. */
    private final MutableLiveData<Integer> mensajeActualizadoIndex = new MutableLiveData<>();

    /**
     * Devuelve el índice del mensaje cuyo estado fue modificado.
     * Ideal para que la UI actualice solo el item afectado.
     *
     * @return LiveData con el índice del mensaje actualizado
     */
    public LiveData<Integer> getMensajeActualizadoIndex() {
        return mensajeActualizadoIndex;
    }

    /** Lista local de mensajes recibidos o enviados en la conversación. */
    private List<MensajeDTO> listaMensajes = new ArrayList<>();

    /**
     * Asigna una lista completa de mensajes al ViewModel.
     *
    // * @param mensajes Lista de mensajes actualizada
     */
    //public void setListaMensajes(List<MensajeDTO> mensajes) {
    //    this.listaMensajes = mensajes;
   // }

    /**
     * Actualiza el estado de un mensaje por ID, marcándolo como leído, respondido, etc.
     * Notifica a la UI el índice del mensaje actualizado.
     *
     * @param mensajeId ID del mensaje a modificar
     * @param nuevoEstado Nuevo estado a asignar
     */
    public void actualizarEstadoMensaje(int mensajeId, int nuevoEstado) {
        for (int i = 0; i < listaMensajes.size(); i++) {
            MensajeDTO m = listaMensajes.get(i);
            if (m.getMensajeId() == mensajeId) {
                m.setEstadoId(nuevoEstado);
                mensajeActualizadoIndex.setValue(i); //notifica el índice actualizado
                break;
            }
        }
    }

}
