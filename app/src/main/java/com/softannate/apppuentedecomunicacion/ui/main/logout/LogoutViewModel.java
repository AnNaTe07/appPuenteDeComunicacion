package com.softannate.apppuentedecomunicacion.ui.main.logout;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.softannate.apppuentedecomunicacion.base.ViewModelBase;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.modelos.dto.RefreshTokenDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutViewModel extends ViewModelBase {

    private Context context;

    public LogoutViewModel(@NonNull Application application) {
        super(application);
    }

    public void logoutEnBackend() {
        String refreshToken = SpManager.getRefreshToken(context);
        if (refreshToken != null && !refreshToken.isEmpty()) {

            Log.d("Logout", refreshToken);
            RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken);
            Call<Void> logoutCall = endpoints.logout(refreshTokenDto);
            logoutCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        SpManager.clearTokens(context);
                        Log.d("Logout", "Sesión cerrada correctamente.");
                    } else {
                        Log.e("Logout", "Error al cerrar sesión: " + response.message());
                        mensaje.setValue("Error al cerrar sesión: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("Logout", "Error de conexión: " + t.getMessage());
                    mensaje.setValue("Error de conexión: " + t.getMessage());
                }
            });
        } else {
            // Si no hay refreshToken, limpiar tokens y sale directamente
            SpManager.clearTokens(context);
        }
    }
}
