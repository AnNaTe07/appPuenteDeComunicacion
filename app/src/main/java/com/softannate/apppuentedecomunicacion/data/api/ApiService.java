package com.softannate.apppuentedecomunicacion.data.api;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.data.local.TokenExpirationChecker;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Servicio de red centralizado para la configuración y obtención de clientes Retrofit.
 * <p>
 * Provee instancias para consumir endpoints de la API principal y para autenticación.
 * Usa {@link AuthInterceptor} para incluir autenticación basada en tokens en las solicitudes.
 */
public class ApiService {

    /** URL base para todas las solicitudes a la API. */
    public static final String BASE_URL = "http://192.168.1.6:5001/api/";
    private static Retrofit retrofit;
    private static Retrofit authRetrofit; // Instancia separada para AuthService

    /**
     * Obtiene una instancia de {@link Endpoints} para realizar llamadas a la API principal.
     * Si no existe aún, configura Retrofit con interceptores y convertidores necesarios.
     *
     * @param spManager Instancia de gestor de almacenamiento local
     * @param context Contexto de aplicación
     * @param tokenExpirationChecker Verificador de expiración de tokens
     * @return Cliente de la API principal con autenticación incluida
     */
    public static Endpoints getApi(SpManager spManager, Context context, TokenExpirationChecker tokenExpirationChecker) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(spManager, context, getAuthService(), tokenExpirationChecker))
                    .build();

            Gson gson = new GsonBuilder().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit.create(Endpoints.class);
    }

    /**
     * Retorna una instancia de {@link AuthService} para realizar operaciones relacionadas a autenticación.
     * Esta instancia de Retrofit no incluye el interceptor de autenticación.
     *
     * @return Cliente AuthService para autenticación (login, refresh, etc.)
     */
    public static AuthService getAuthService() {
        if (authRetrofit == null) {
            Gson gson = new GsonBuilder().create();
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return authRetrofit.create(AuthService.class);
    }
}
