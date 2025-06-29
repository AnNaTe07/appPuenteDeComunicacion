package com.softannate.apppuentedecomunicacion.data.api;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.data.local.TokenExpirationChecker;
import com.softannate.apppuentedecomunicacion.modelos.dto.TokenDto;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Call;

public class AuthInterceptor implements Interceptor {

    private final SpManager spManager;
    private final Context context;
    private final AuthService authService;
    private final TokenExpirationChecker tokenExpirationChecker;

    public AuthInterceptor(SpManager spManager, Context context, AuthService authService, TokenExpirationChecker tokenExpirationChecker) {
        this.spManager = spManager;
        this.context = context;
        this.authService = authService;
        this.tokenExpirationChecker = tokenExpirationChecker;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String accessToken = spManager.getAccessToken(context);
        String refreshToken = spManager.getRefreshToken(context);

        Log.d("AuthInterceptor", "AccessToken: " + accessToken);
        Log.d("AuthInterceptor", "RefreshToken: " + refreshToken);

        if (needsAuthorization(originalRequest)) {
            if (isTokenVacioOExpirado(accessToken)) {
                // No hay token válido: limpio y evito hacer la petición
                spManager.clearTokens(context);
                Log.d("AuthInterceptor", "No hay token válido, tokens borrados.");
            } else {
                if (tokenExpirationChecker.isTokenExpired(context)) {
                    Log.d("AuthInterceptor", "AccessToken ha expirado.");

                    if (!isTokenVacioOExpirado(refreshToken) && !tokenExpirationChecker.isRefreshTokenExpired(context)) {
                        Log.d("AuthInterceptor", "RefreshToken aún es válido, intentando renovar...");

                        synchronized (this) {
                            String currentAccessToken = spManager.getAccessToken(context);

                            if (tokenExpirationChecker.isTokenExpired(context)) {
                                accessToken = renovarAccessToken(refreshToken);
                                if (accessToken == null) {
                                    spManager.clearTokens(context);
                                    Log.d("AuthInterceptor", "Fallo al renovar token, tokens borrados.");
                                }
                            } else {
                                accessToken = currentAccessToken; // Otro hilo lo renovó
                            }
                        }
                    } else {
                        spManager.clearTokens(context);
                        Log.d("AuthInterceptor", "RefreshToken inválido o expirado. Tokens borrados.");
                    }
                }
                // agrego el header con accessToken actualizado
                if (!isTokenVacioOExpirado(accessToken)) {
                    originalRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + accessToken)
                            .build();
                }
            }
        }

        return chain.proceed(originalRequest);
    }

    // ----------- MÉTODOS AUXILIARES -----------

    private boolean needsAuthorization(Request request) {
        String url = request.url().toString();
        return !url.endsWith("/auth/login");
    }

    private boolean isTokenVacioOExpirado(String token) {
        return token == null || token.isEmpty();
    }

    private String renovarAccessToken(String refreshToken) {
        try {
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("refresh_token", refreshToken);
            Call<TokenDto> refreshTokenCall = authService.refreshTokenSync(jsonBody);

            retrofit2.Response<TokenDto> response = refreshTokenCall.execute();

            Log.d("AuthInterceptor", "Headers de la solicitud: " + refreshTokenCall.request().headers());
            Log.d("AuthInterceptor", "Cuerpo exacto de la solicitud: " + jsonBody.toString());

            if (response.isSuccessful() && response.body() != null && response.body().getAccessToken() != null) {
                String newAccessToken = response.body().getAccessToken();
                String newRefreshToken = response.body().getRefreshToken();

                spManager.setAccessToken(context, newAccessToken);
                spManager.setRefreshToken(context, newRefreshToken);

                return newAccessToken;
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                Log.e("AuthInterceptor", "Fallo renovación. Código: " + response.code());
                Log.e("AuthInterceptor", "Error: " + errorBody);
                spManager.clearTokens(context);
                return null;
            }

        } catch (IOException e) {
            Log.e("AuthInterceptor", "Error al renovar token: " + e.getMessage());
            spManager.clearTokens(context);
            return null;
        }
    }
}
