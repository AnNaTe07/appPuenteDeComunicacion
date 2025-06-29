package com.softannate.apppuentedecomunicacion.ui.login;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import com.softannate.apppuentedecomunicacion.base.ViewModelPass;
import com.softannate.apppuentedecomunicacion.modelos.dto.LoginResponseDTO;
import com.softannate.apppuentedecomunicacion.modelos.dto.OlvidaPassDTO;
import com.softannate.apppuentedecomunicacion.ui.main.MainActivity;
import com.softannate.apppuentedecomunicacion.modelos.dto.LoginDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityViewModel extends ViewModelPass {

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean validarPassword(String password){
        if (password.isEmpty()) {
            mensaje.setValue("Por favor, ingrese su contraseña");
            return false;
        }

        if (password.length() < 8) {
            mensaje.setValue("La contraseña debe tener al menos 8 caracteres");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            mensaje.setValue("La contraseña debe contener al menos una letra mayúscula");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            mensaje.setValue("La contraseña debe contener al menos un número");
            return false;
        }

      /*  if (!password.matches(".*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/].*")) {
            mensaje.setValue("La contraseña debe contener al menos un carácter especial");
            return false;
        }
       */
        return true;
    }

    public void validarLogin(String email, String password){
        if(!validarEmail(email) || !validarPassword(password)){
            return;
        }
        iniciarSesion(email, password);
    }

    public void iniciarSesion(String email, String password) {

        Log.d("Login", "Iniciando sesión con email: " + email);

        LoginDTO usuario = new LoginDTO(email, password);

        Call<LoginResponseDTO> call = endpoints.login(usuario);
        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, retrofit2.Response<LoginResponseDTO> response) {
                if (response.isSuccessful()&& response.body() != null) {

                    LoginResponseDTO body = response.body();
                   // Log.d("Login", "Response: " + body.toString());

                    spManager.setAccessToken(getApplication(), body.getToken());
                    spManager.setExpiracionToken(getApplication(), body.getExpiracion_token());
                    spManager.setRefreshToken(getApplication(), body.getRefresh_token());
                    spManager.setExpiracionRefreshToken(getApplication(), body.getRefresh_expiracion());
                    spManager.setId(getApplication(), String.valueOf(body.getId()));
                    spManager.setRol(getApplication(), String.valueOf(body.getRol().getId()));
                    spManager.setEmail(getApplication(), body.getEmail());
                    spManager.setNombreCompleto(getApplication(), body.getNombreCompleto());

                    //Log.d("Login", "Rol: " + body.getRol().getId() + ", Email: " + body.getEmail() + ", Nombre: " + body.getNombreCompleto() + ", Token: " + body.getToken() + ", RefreshToken: " + body.getRefresh_token() + ", ExpiracionToken: " + body.getExpiracion_token() + ", ExpiracionRefreshToken: " + body.getRefresh_expiracion() + ", Id: " + body.getId() + ", NombreCompleto: " + body.getNombreCompleto() + ", Rol: " + body.getRol().getId() + ", RolNombre: " + body.getRol().getNombre());

                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.putExtra("login", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getApplication().startActivity(intent);
                    mensaje.setValue("Bienvenido "+body.getNombreCompleto());
                    }else {
                    //Log.e("Login", "Error en la respuesta: " + response.message());
                    mensaje.setValue("Credenciales incorrectas");
                }
            }
            @Override
            public void onFailure(Call<LoginResponseDTO>call, Throwable t) {
                //Log.e("LoginError", "Error: " + t.getMessage());
                mensaje.setValue("Error al iniciar sesión. Por favor verifica tu conexión.");
            }
        });
    }

    public void validarRestablecerPass(String email) {
        if (!validarEmail(email)) {
            return;
        }
      enviarEmailParaRestablecer(email);
    }

    public void enviarEmailParaRestablecer(String email) {
        enviandoMensaje.setValue(true);
        OlvidaPassDTO olvidaPass = new OlvidaPassDTO(email);

        Call<ResponseBody> call = endpoints.enviarEmail(olvidaPass);
        Log.d("Login", "Iniciando sesión con email: " + email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    mensaje.setValue("Email enviado para restablecer la contraseña");
                } else {
                    Log.d("Login", "Error en la respuesta: " + response.message());
                    mensaje.setValue("Email no encontrado");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               mensaje.setValue("Error al iniciar sesión. Por favor verifica tu conexión.");
            }
        });
    }
}
