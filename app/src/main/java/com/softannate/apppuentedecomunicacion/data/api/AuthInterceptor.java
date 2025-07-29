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

/**
 * Interceptor para gestionar la autenticación de solicitudes HTTP en Retrofit usando tokens.
 * <p>
 * Verifica si el access token está expirado y lo renueva automáticamente utilizando un refresh token,
 * siempre que este sea válido. Si ambos tokens son inválidos, los elimina de la sesión.
 * <p>
 * Este interceptor agrega el header {@code Authorization} con el access token en las solicitudes protegidas.
 */
public class AuthInterceptor implements Interceptor {

    private final SpManager spManager;
    private final Context context;
    private final AuthService authService;
    private final TokenExpirationChecker tokenExpirationChecker;

    /**
     * Constructor que inyecta los componentes necesarios para el manejo de tokens.
     *
     * @param spManager             gestor de almacenamiento de tokens
     * @param context               contexto de aplicación para acceder a preferencias
     * @param authService           cliente de autenticación (Retrofit)
     * @param tokenExpirationChecker verificador de expiración de tokens
     */
    public AuthInterceptor(SpManager spManager, Context context, AuthService authService, TokenExpirationChecker tokenExpirationChecker) {
        this.spManager = spManager;
        this.context = context;
        this.authService = authService;
        this.tokenExpirationChecker = tokenExpirationChecker;
    }

    /**
     * Intercepta cada solicitud HTTP y agrega el header de autorización si es necesario.
     * <p>
     * Si el access token ha expirado, intenta renovarlo con el refresh token de forma segura.
     * En caso de fallo, limpia los tokens de la sesión.
     *
     * @param chain cadena de solicitud de OkHttp
     * @return respuesta HTTP con o sin modificación en los headers
     * @throws IOException si ocurre un error en la comunicación
     */
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

    // ----------------------------------- MÉTODOS AUXILIARES -------------------------------------

    /**
     * Determina si una solicitud requiere autenticación.
     *
     * @param request solicitud actual
     * @return {@code true} si requiere token, {@code false} si no
     */
    private boolean needsAuthorization(Request request) {
        String url = request.url().toString();
        return !url.endsWith("/auth/login");
    }

    /**
     * Verifica si el token es nulo o vacío.
     *
     * @param token cadena del token a evaluar
     * @return {@code true} si está vacío o nulo, {@code false} si contiene valor
     */
    private boolean isTokenVacioOExpirado(String token) {
        return token == null || token.isEmpty();
    }

    /**
     * Solicita un nuevo access token usando el refresh token actual.
     * <p>
     * Si la renovación falla, los tokens se eliminan de la sesión.
     *
     * @param refreshToken token de renovación
     * @return nuevo access token o {@code null} si falla
     */
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
