package com.softannate.apppuentedecomunicacion.request;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.softannate.apppuentedecomunicacion.modelos.EstadoAsistencia;
import com.softannate.apppuentedecomunicacion.modelos.Login;
import com.softannate.apppuentedecomunicacion.modelos.Materia;
import com.softannate.apppuentedecomunicacion.modelos.Nivel;
import com.softannate.apppuentedecomunicacion.modelos.OlvidaPass;
import com.softannate.apppuentedecomunicacion.modelos.Rol;
import com.softannate.apppuentedecomunicacion.modelos.TipoActividad;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class ApiClient {
    public static final String BASE_URL = "http://192.168.1.2:5000/api/";

    public static Endpoints getApi(){
        Gson gson = new GsonBuilder().setLenient().create();//prseo del elemento gson a objeto java

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))//para objetos complejos como fechas
                .build();
        return retrofit.create(Endpoints.class);
    }

    public interface Endpoints{

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

        //TiposActividades
        @GET("tipoActividad")
        Call<List<TipoActividad>> tiposActividades();

        //Login
        @POST("usuario/login")
        Call<Map<String,Object>> login(@Body Login login);

        //Olvidé Pass
        @POST("usuario/olvidePass")
        Call<ResponseBody> enviarEmail(@Body OlvidaPass dto);


    }


    public static void guardarToken(String token,String rol, String nombre, String email, String id, String expiracion, Context context){
        SharedPreferences sp=context.getSharedPreferences("datosUsuario.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sp.edit();

        //guardar datos del usuario
        editor.putString("token", token);
        editor.putString("role",rol);
        editor.putString("fullName",nombre);
        editor.putString("email", email);
        editor.putString("id",id);
        editor.putString("expiration", expiracion);

        editor.apply();
    }
    public static String getToken(Context context){
        SharedPreferences sp= context.getSharedPreferences("datosUsuario.xml", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }
    public static String  getRol(Context context){
        SharedPreferences sp= context.getSharedPreferences("datosUsuario.xml",Context.MODE_PRIVATE);
        return sp.getString("rol", null);
    }
    public static String getNombre(Context context){
        SharedPreferences sp= context.getSharedPreferences("datosUsuario.xml", Context.MODE_PRIVATE);
        return sp.getString("nombre", null);
    }
    public static String getEmail( Context context){
        SharedPreferences sp= context.getSharedPreferences("datosUsuario.xml",Context.MODE_PRIVATE);
        return sp.getString("email", null);
    }
    public static String getId(Context context){
        SharedPreferences sp= context.getSharedPreferences("datosUsuario.xml", Context.MODE_PRIVATE);
        return  sp.getString("id", null);
    }
    public static String getExpiracion(Context context){
        SharedPreferences sp= context.getSharedPreferences("datosUsuario.xml", Context.MODE_PRIVATE);
        return sp.getString("expiracion", null);
    }
}
