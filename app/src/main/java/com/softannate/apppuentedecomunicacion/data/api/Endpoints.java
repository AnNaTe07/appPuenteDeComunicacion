package com.softannate.apppuentedecomunicacion.data.api;


import com.softannate.apppuentedecomunicacion.modelos.CambioPass;
import com.softannate.apppuentedecomunicacion.modelos.Categoria_Mensaje;
import com.softannate.apppuentedecomunicacion.modelos.EstadoAsistencia;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.LoginDTO;
import com.softannate.apppuentedecomunicacion.modelos.dto.LoginResponseDTO;
import com.softannate.apppuentedecomunicacion.modelos.Materia;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.modelos.Nivel;
import com.softannate.apppuentedecomunicacion.modelos.dto.OlvidaPassDTO;
import com.softannate.apppuentedecomunicacion.modelos.RestablecerPass;
import com.softannate.apppuentedecomunicacion.modelos.Rol;
import com.softannate.apppuentedecomunicacion.modelos.dto.TokenFirebase;
import com.softannate.apppuentedecomunicacion.modelos.dto.UsuarioDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.PreferenciaNotificacionDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.RefreshTokenDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.UserUpdateDto;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Endpoints {

    //--------------------------------------------auth--------------------------------------------
    /**
     * Realiza el inicio de sesión enviando credenciales al endpoint {@code auth/login}.
     * <p>
     * Este método debe recibir un objeto {@link LoginDTO} con los datos del usuario
     * y retorna una respuesta con información del acceso autenticado dentro de un {@link LoginResponseDTO}.
     */
    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginDTO login);

    //Logout - Funcionando
    @POST("auth/logout")
    Call<Void> logout(@Body RefreshTokenDto refreshToken);

    //-----------------------------------  ------usuario-------------------------------------------

    //Olvidé Pass
    @POST("usuario/olvidoPass")
    Call<ResponseBody> enviarEmail(@Body OlvidaPassDTO dto);

    //Profile - Funcionando
    @GET("usuario/profile")
    Call<UsuarioDto> profile();

    //Update - Funcionando
    @PUT("usuario/update")
    Call<UsuarioDto> update(@Body UserUpdateDto usuario);


    //envio token Firebase - Funcionando
    @POST("usuario/token_firebase")
    Call<Void> actualizarToken(@Body TokenFirebase request);

    //--------------------------------------------mensaje--------------------------------------------
    //Mensajes - Funcionando
    @GET("mensaje")
    Call<List<MensajeDTO>> mensajes();

    //Destinatarios por alumno - Funcionando
    @POST("mensaje/alumno/{alumnoId}")
    Call<List<DestinatarioDto>> destinatariosPorAlumno(@Path("alumnoId") int alumnoId);

    //Conversacion - Funcionando
    @GET("mensaje/{receptorId}")
    Call<List<MensajeDTO>> conversacion(
            @Path("receptorId") int receptorId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("mensaje/{receptorId}")
    Call<List<MensajeDTO>> conversacionConAlumno(
            @Path("receptorId") int receptorId,
            @Query("alumnoId") int alumnoId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("mensaje/{receptorId}")
    Call<List<MensajeDTO>> conversacionAjenaConAlumno(
            @Path("receptorId") int receptorId,
            @Query("alumnoId") int alumnoId,
            @Query("otroParticipanteId") int otroParticipanteId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    //Nuevo Mensaje Educativo
    @Multipart
    @POST("mensaje/enviarEducacional")
    Call<Void> enviarMensajeEducativo( @Part("payload") RequestBody mensaje,
                                       @Part List<MultipartBody.Part> archivos);

    //Nuevo Mensaje Tutor - Funcionando
    @Multipart
    @POST("mensaje/enviarTutor")
    Call<Void> enviarMensajeTutor(@Part("payload") RequestBody mensaje,
                                  @Part List<MultipartBody.Part> archivos);

    //Conversacion, marcar como leído - Funcionando
    @PUT("mensaje/leidos")
    Call<Void> mensajesLeidos(@Query("emisorId") int receptorId);

    @PUT("mensaje/leidos")
    Call<Void> mensajesLeidosConAlumnos(@Query("emisorId") int receptorId,@Query("alumnoId") int alumnoId );

    //Buscar - Funcionando
    @GET("mensaje/buscar")
    Call<List<MensajeDTO>> buscar( @Query("busqueda") String busqueda, @Query("fechaInicio") String fechaInicio, @Query("fechaFin") String fechaFin );

    //Obtener personal educativo - Funcionando
    @GET("mensaje/destinatarioEducativo")
    Call<List<DestinatarioDto>> obtenerDestinatariosEducativos();

    //archivados
    @GET("mensaje/archivados")
    Call<List<MensajeDTO>> obtenerArchivados();


    //------------------------------------------categoria------------------------------------------

    //Categorias - Funcionando
    @GET("categoria_mensaje")
    Call<List<Categoria_Mensaje>> categorias();

    //------------------------------------------notificacion------------------------------------------

    //Preferencias de Notificacion - Funcionando
    @POST("notificacion/actualizar")
    Call<Void> actualizarNotificacion(@Body PreferenciaNotificacionDto dto);

    //------------------------------------------curso------------------------------------------

    //Cursos - Funcionando
    @GET("curso/cursos")
    Call<List<CursoDto>> obtenerCursos();

    //Alumnos - Funcionando
    @GET("curso/{cursoId}")
    Call<List<AlumnoDto>> obtenerAlumnosDeCurso(@Path("cursoId") int cursoId);

    //Agenda
    @GET("curso/buscar-alumnos")
    Call<List<AlumnoDto>> buscarAlumnos(@Query("texto") String texto);


    //------------------------------------------alumno------------------------------------------

    //Obtener alumnos - Funcionando
    @GET("alumno")
    Call<List<AlumnoDto>> alumnos();


    //------------------------------------------archivo------------------------------------------

    //Descargar Archivo - Funcionando
    @GET
    Call<ResponseBody> descargarArchivoDesdeFirebase(@Url String archivoUrl);

    @GET("archivo_adjunto/{id}")
    Call<ResponseBody> descargarArchivoDesdeApi(@Path("id") int id);








    //Niveles
    @GET("nivel")
    Call<List<Nivel>> niveles();

    //EstadosAsistencias
    @GET("estadoAsistencia")
    Call<List<EstadoAsistencia>> estadosAsistencias();

    //Materias
    @GET("materia")
    Call<List<Materia>> materias();

    //Roles
    @GET("rol")
    Call<List<Rol>> roles();

    //Restablecer Pass
    @POST("usuario/restablecerPass")
    Call<ResponseBody> restablecerPass(@Body RestablecerPass dto, @Header("Authorization") String token);

    //Avatar
    @PATCH("usuario/avatar")
    @Multipart
    Call<ResponseBody> updateAvatar(@Header("Authorization") String token, @Part MultipartBody.Part file
    );

    //Eliminar Avatar
    @DELETE("usuario/avatar")
    Call<String> deleteAvatar(@Header("Authorization") String token);

    //Cambiar Pass
    @PATCH("usuario/pass")
    Call<ResponseBody> cambiarPass(@Header("Authorization") String token, @Body CambioPass pass);

}


