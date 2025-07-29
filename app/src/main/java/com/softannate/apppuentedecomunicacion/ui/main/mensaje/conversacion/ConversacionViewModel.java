package com.softannate.apppuentedecomunicacion.ui.main.mensaje.conversacion;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.ViewModelConversacionNuevo;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.modelos.Archivo_adjunto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeEducativoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeTutorDto;
import com.softannate.apppuentedecomunicacion.utils.CustomTypeFace;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel que gestiona la lógica de una conversación entre usuarios, incluyendo
 * mensajes, participantes, alumno involucrado y visibilidad del layout en base
 * al contexto del usuario. Permite cargar mensajes paginados desde distintas
 * fuentes según si el usuario participa o solo visualiza como director.
 */
public class ConversacionViewModel extends ViewModelConversacionNuevo {

    //private MutableLiveData<List<MensajeDTO>> listaMensajes = new MutableLiveData<>();
    private int alumnoId, usuarioId, emisorId, receptorId;
    private int idVisual;
    private MutableLiveData<Boolean> mostrarLayout=new MutableLiveData<>();;

    private boolean cargando = false;
    private boolean finDeConversacion = false;

    private MutableLiveData<List<MensajeDTO>> listaMensajes = new MutableLiveData<>(new ArrayList<>());

    /**
     * Constructor. Ejecuta la carga inicial de categorías.
     * @param application Contexto de la aplicación
     */
    public ConversacionViewModel(@NonNull Application application) {
        super(application);
        obtenerCategorias();
    }

    /**
     * Devuelve la lista observable de mensajes de la conversación.
     * @return LiveData de los mensajes actuales
     */
    public LiveData<List<MensajeDTO>> getListaMensaje() {
        return listaMensajes;
    }

    /**
     * Devuelve el ID que se usa para visualizar la conversación (depende del rol).
     * @return idVisual
     */
    public int getIdVisual() {
        return idVisual;
    }

    /**
     * Configura los IDs cuando el usuario participa activamente en la conversación.
     * @param usuarioId ID del usuario logueado
     * @param alumnoId ID del alumno involucrado (opcional)
     */
    public void setIds(int usuarioId, int alumnoId) {
        this.usuarioId = usuarioId;
        this.alumnoId = alumnoId;
        this.idVisual = usuarioId;
        cargarMensajes(0);
    }
    /**
     * Configura los IDs cuando un director visualiza una conversación ajena.
     * @param emisorId ID del emisor del mensaje
     * @param receptorId ID del receptor del mensaje
     * @param alumnoId ID del alumno involucrado
     */
    public void setIdsParaDirector(int emisorId, int receptorId, int alumnoId) {
        this.emisorId = emisorId;
        this.receptorId = receptorId;
        this.alumnoId = alumnoId;
        this.idVisual = emisorId;
    }

    /**
     * Devuelve el LiveData que controla si mostrar el layout de mensajes.
     * @return MutableLiveData<Boolean> para visibilidad
     */
    public LiveData<Boolean> mostrarLayout() {
        if(mostrarLayout==null){
            mostrarLayout= new MutableLiveData<>();
        }
        return mostrarLayout;
    }

    /**
     * Carga los mensajes de la conversación de forma paginada, evitando duplicados,
     * ordenando cronológicamente y actualizando el estado visual.
     * @param page Número de página a cargar (pag. 0 es inicio)
     */
    public void cargarMensajes(int page) {
        int pageSize = 35;
        Call<List<MensajeDTO>> call;
        if (emisorId != 0 && receptorId != 0 && usuarioId != emisorId && usuarioId != receptorId) {
            //  Director viendo una conversación ajena
            call = endpoints.conversacionAjenaConAlumno(receptorId, alumnoId, emisorId, page, pageSize);
            Log.d("ConversacionViewModel1ajena", "emisorId: " + emisorId + " receptorId: " + receptorId + " alumnoId: " + alumnoId);

        } else {
            //  Usuario (o director) participando en la conversación
            if (alumnoId == 0) {
                call = endpoints.conversacion(usuarioId, page, pageSize);
                Log.d("ConversacionViewModel1alumno0", "usuarioId: " + usuarioId + " alumnoId: " + alumnoId);

            } else {
                call = endpoints.conversacionConAlumno(usuarioId, alumnoId, page, pageSize);
                Log.d("ConversacionViewModel1normal", "usuarioId: " + usuarioId + " alumnoId: " + alumnoId);
            }
        }

        Log.d("ConversacionViewModel1", "emisorId: " + emisorId + " receptorId: " + receptorId + " alumnoId: " + alumnoId);
       // Call<List<MensajeDTO>> call = endpoints.conversacion(usuarioId, alumnoId);
        call.enqueue(new Callback<List<MensajeDTO>>() {
            @Override
            public void onResponse(Call<List<MensajeDTO>> call, Response<List<MensajeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MensajeDTO> nuevos = response.body();
                    List<MensajeDTO> actuales = listaMensajes.getValue();
                    if (actuales == null) actuales = new ArrayList<>();

                   //  unir sin duplicados
                    List<MensajeDTO> resultado = new ArrayList<>();
                    for (MensajeDTO m : nuevos) {
                        boolean duplicado = false;
                        for (MensajeDTO a : actuales) {
                            if (a.getMensajeId() == m.getMensajeId()) {
                                duplicado = true;
                                break;
                            }
                        }
                        if (!duplicado) resultado.add(m);
                    }
                    resultado.addAll(actuales);

                   //  ordeno por ID ya que incrementan con el tiempo
                    resultado.sort(Comparator.comparingInt(MensajeDTO::getMensajeId));

                    listaMensajes.setValue(resultado);

                    if (nuevos.size() < pageSize) finDeConversacion = true;
                    cargando = false;

                    Log.d("", "Cargando mensajes: " + response.body());
                } else {
                    cargando = false;
                }
            }

            @Override
            public void onFailure(Call<List<MensajeDTO>> call, Throwable throwable) {
                cargando = false;
            }
        });
    }

    /**
     * Funciones adicionales del ViewModel de conversación para gestionar el estado,
     * inicialización de participantes, marcación de mensajes como leídos y configuración
     * de destinatarios para el envío de nuevos mensajes.
     */

    /**
     * Marca todos los mensajes actuales como leídos según el contexto del alumno.
     * Realiza una llamada al endpoint correspondiente para actualizar el estado de lectura.
     */
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

    /**
     * Inicializa el ViewModel desde un Bundle recibido por navegación.
     * Define si el usuario participa activamente o está visualizando como director,
     * y configura visibilidad del layout correspondiente.
     *
     * @param context Contexto de la aplicación.
     * @param args Bundle con argumentos de navegación.
     */
    public void inicializarDesdeBundle(Context context, Bundle args) {
        int alumnoId = args.getInt("alumnoId");
        int emisorId = args.getInt("emisorId", -1);
        int receptorId = args.getInt("receptorId", -1);
        int userLogueadoId = Integer.parseInt(SpManager.getId(context));
        String rol = SpManager.getRol(context);

        if (emisorId == -1 || receptorId == -1) {
            // Fallback (por si viene solo un userId viejo)
            int userId = args.getInt("userId");
            setIds(userId, alumnoId);
            mostrarLayout.setValue(true);
            return;
        }

        if (userLogueadoId == emisorId || userLogueadoId == receptorId) {
            //  Caso 1 o 2: usuario (común o director) participa
            int otro = (userLogueadoId == emisorId) ? receptorId : emisorId;
            setIds(otro, alumnoId);
            mostrarLayout.setValue(true);
        } else if ("2".equals(rol)) {
            //  Caso 3: director viendo conversación ajena
            setIdsParaDirector(emisorId, receptorId, alumnoId);
            mostrarLayout.setValue(false);
        } else {
            //  Esto nunca debería pasar (usuario no autorizado a ver conversación ajena)
            Log.e("Conversacion", "Usuario sin permisos para esta conversación.");
        }
    }


    //destinatario para enviar el mensaje
    /**
     * Configura los destinatarios para el envío de mensajes según el rol del usuario.
     * Se determina si el destinatario es directo, si está vinculado a un alumno o si
     * debe limpiarse el listado de alumnos.
     *
     * @param usuarioId ID del usuario destinatario.
     * @param alumnoId ID del alumno involucrado (si aplica).
     * @param rol Rol del usuario logueado.
     */
    public void prepararDestinatarios(int usuarioId, int alumnoId, int rol) {
       // String rol = SpManager.getRol(getApplication());
        if (rol == 5) {
            setAlumnoDirecto(alumnoId);

            DestinatarioDto u = new DestinatarioDto();
            u.setId(usuarioId);
            u.setSeleccionado(true);
            setDestinatariosSeleccionados(List.of(u));
            limpiarAlumnos();

        } else {
            if (alumnoId > 0) {
                setAlumnoDirecto(alumnoId);
                setDestinatariosSeleccionados(Collections.emptyList());
            } else if (usuarioId > 0) {
                DestinatarioDto u = new DestinatarioDto();
                u.setId(usuarioId);
                u.setSeleccionado(true);
                setDestinatariosSeleccionados(List.of(u));
                limpiarAlumnos();
            }
        }
    }

    /**
     * Funciones del ViewModel que permiten enviar mensajes, gestionar archivos adjuntos y
     * controlar el estado de descarga y visualización dentro de la conversación.
     */

    /**
     * Envía un mensaje según el rol del usuario.
     * Si es tutor (rol 5), utiliza la estructura de mensaje tipo Tutor.
     * Para otros roles, usa el formato educativo estándar.
     *
     * @param context Contexto de la aplicación.
     * @param mensaje Contenido del mensaje.
     * @param idEtiqueta Etiqueta asociada (categoría o tipo de mensaje).
     * @param alumno ID del alumno involucrado (si aplica).
     * @param rol Rol del usuario logueado.
     */
    public void enviarMensaje(Context context, String mensaje, int idEtiqueta, int alumno, int rol){
        if (rol==5){
            MensajeTutorDto mensajeRequest = construirPayloadTutor(mensaje, idEtiqueta, alumno);
            enviarMensajeTutor(context, mensajeRequest);
        } else {
            MensajeEducativoDto mensajeRequest = construirPayload(mensaje, idEtiqueta, false);
            enviarMensajeEducativo(context, mensajeRequest);
        }
    }

  //  private final MutableLiveData<File> archivoDescargado = new MutableLiveData<>();

    /**
     * Devuelve un observable para monitorear la descarga de archivos,
     * incluyendo el archivo físico y su tipo MIME para visualización.
     *
     * @return LiveData que expone la información del archivo descargado.
     */
    public LiveData<Pair<File, String>> getArchivoDescargado() {
        return archivoDescargado;
    }
    private final MutableLiveData<Pair<File, String>> archivoDescargado = new MutableLiveData<>();

    /**
     * Gestiona el proceso de descarga y apertura de un archivo adjunto.
     * Si el archivo ya existe localmente, pregunta si debe volver a descargarse.
     * Si no existe, inicia la descarga desde el servidor.
     *
     * @param context Contexto de la aplicación.
     * @param archivo Objeto con información del archivo adjunto.
     */
    public void descargarYAbrirArchivo(Context context, Archivo_adjunto archivo) {
        File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directorio.exists()) directorio.mkdirs();
        String nombreSeguro = archivo.getNombre().replaceAll("[^a-zA-Z0-9._-]", "_");
        File archivoLocal = new File(directorio, nombreSeguro);

        Log.d("ARCHIVO-LOCAL", " Nombre original: " + archivo.getNombre());
        Log.d("ARCHIVO-LOCAL", " Nombre seguro: " + nombreSeguro);
        Log.d("ARCHIVO-LOCAL", " Ruta completa: " + archivoLocal.getAbsolutePath());
        Log.d("ARCHIVO-LOCAL", " Existe: " + archivoLocal.exists());
        Log.d("ARCHIVO-LOCAL", " Tamaño: " + archivoLocal.length());

        if (archivoLocal.exists() && archivoLocal.length() > 0) {
            mostrarDialogo(context, archivo, archivoLocal);
        } else {
            descargarArchivo(context, archivo, archivoLocal);
        }
    }

    /**
     * Descarga el archivo desde el endpoint y lo guarda en el almacenamiento local.
     * Notifica mediante LiveData cuando la descarga termina correctamente.
     *
     * @param context Contexto de la aplicación.
     * @param archivo Objeto con datos del archivo.
     * @param destino Ruta local donde se guardará el archivo.
     */
    private void descargarArchivo(Context context, Archivo_adjunto archivo, File destino) {
       // Call<ResponseBody> call = endpoints.descargarArchivoDesdeFirebase(archivo.getUrl());
        Log.d("ARCHIVO", " URL: " + archivo.getId());
        Call<ResponseBody> call = endpoints.descargarArchivoDesdeApi(archivo.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        FileOutputStream fos = new FileOutputStream(destino);
                        fos.write(response.body().bytes());
                        fos.close();

                        String mime = obtenerMimeType(destino.getName(), archivo.getTipoMime());
                        archivoDescargado.setValue(new Pair<>(destino, mime));
                        mensaje.setValue("Archivo descargado correctamente");

                    } catch (Exception e) {
                        mensaje.setValue("Error guardando archivo");
                    }
                } else {
                    mensaje.setValue("No se pudo descargar");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mensaje.setValue("Fallo de red: " + t.getMessage());
            }
        });
    }

    /**
     * Determina el MIME type del archivo a partir de su nombre o metadata provista.
     *
     * @param nombreArchivo Nombre del archivo local.
     * @param mimeDto MIME directo desde el DTO (si existe).
     * @return Tipo MIME del archivo para apertura.
     */
    private String obtenerMimeType(String nombreArchivo, String mimeDto) {
        if (mimeDto != null && !mimeDto.isEmpty()) return mimeDto;

        String ext = MimeTypeMap.getFileExtensionFromUrl(nombreArchivo);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return (mime != null) ? mime : "*/*";
    }

    /**
     * Muestra un diálogo al usuario para decidir si desea volver a descargar el archivo.
     * Si el usuario elige no descargarlo nuevamente, se reutiliza el archivo local existente.
     *
     * @param context Contexto de la aplicación.
     * @param archivo Metadatos del archivo.
     * @param archivoLocal Archivo local previamente descargado.
     */
    private void mostrarDialogo(Context context, Archivo_adjunto archivo,  File archivoLocal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Typeface tipoLetra = ResourcesCompat.getFont(context, R.font.adamina);

        SpannableString titulo = new SpannableString("Archivo ya descargado");
        titulo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.teal_700)), 0, titulo.length(), 0);
        titulo.setSpan(new CustomTypeFace(tipoLetra), 0, titulo.length(), 0);

        SpannableString texto = new SpannableString("¿Querés volver a descargar \"" + archivo.getNombre() + "\"?");
        texto.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.teal_700)), 0, texto.length(), 0);
        texto.setSpan(new CustomTypeFace(tipoLetra), 0, texto.length(), 0);

        builder.setMessage(texto);
        builder.setTitle(titulo);
        builder.setPositiveButton("Si", (dialog, i) -> {
                    descargarArchivo(context, archivo, archivoLocal);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    String mime = obtenerMimeType(archivoLocal.getName(), archivo.getTipoMime());
                    archivoDescargado.setValue(new Pair<>(archivoLocal, mime));
                    mensaje.setValue("Usando archivo existente");
                });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.teal_700));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.teal_700));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(tipoLetra);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(tipoLetra);
            dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame);
        });
        dialog.show();
    }


}