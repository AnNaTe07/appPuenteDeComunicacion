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

public class ApiService {
    public static final String BASE_URL = "http://192.168.1.6:5001/api/";
    private static Retrofit retrofit;
    private static Retrofit authRetrofit; // Instancia separada para AuthService

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
