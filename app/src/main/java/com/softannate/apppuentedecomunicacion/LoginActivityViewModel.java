package com.softannate.apppuentedecomunicacion;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.apppuentedecomunicacion.modelos.Login;
import com.softannate.apppuentedecomunicacion.modelos.OlvidaPass;
import com.softannate.apppuentedecomunicacion.request.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityViewModel extends AndroidViewModel {

    private MutableLiveData<String> mensaje;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    //mutable mensaje
    public LiveData<String> getMensaje() {
        if (mensaje == null) {
            mensaje = new MutableLiveData<>();
        }
        return mensaje;
    }

    public boolean validarEmail(String email) {
        if (email.isEmpty()) {
            mensaje.setValue("El email es obligatorio");
            return false;
        }
        return true;
    }

    public void validarLogin(String email, String password) {
        if (!validarEmail(email)) {
            return;//salgo si el email no es válido
        }
        if (password.isEmpty()) {
            mensaje.setValue("El password es obligatorio");
            return;//salgo si el pass está vacio
        }

        //si ambos son válidos, llamo al método iniciar sesión
        iniciarSesion(email, password);
    }

    public void iniciarSesion(String email, String password) {

        Log.d("Login", "Iniciando sesión con email: " + email);
        Login usuario = new Login(email, password);
        ApiClient.Endpoints api = ApiClient.getApi();
        Call<Map<String,Object>> call = api.login(usuario);
        call.enqueue(new Callback<Map<String,Object>>() {
            @Override
            public void onResponse(Call<Map<String,Object>> call, retrofit2.Response<Map<String,Object>> response) {
                if (response.isSuccessful()&& response.body() != null) {

                        Map<String,Object> body = response.body();

                    // Log para ver los datos de la respuesta
                    Log.d("Login", "Response: " + body.toString());
                        String token = (String) body.get("token");
                        String rol = (String) body.get("rol");
                        String nombre = (String) body.get("nombre");
                        String email = (String) body.get("email");
                        String id = (String) body.get("id");
                        String expiracion = (String) body.get("expiracion");

                        //guardo los datos en sp
                        ApiClient.guardarToken("Bearer " + token, rol, nombre, email, id, expiracion, getApplication());

                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("login", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getApplication().startActivity(intent);
                        Toast.makeText(getApplication(), "Bienvenido", Toast.LENGTH_SHORT).show();
                    }else {
                    Log.e("Login", "Error en la respuesta: " + response.message());
                    Toast.makeText(getApplication(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Map<String,Object>>call, Throwable t) {
                Toast.makeText(getApplication(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void validarRestablecerPass(String email) {
        if (!validarEmail(email)) {
            return;//salgo si el email no es válido
        }
      envirEmailParaRestablecer(email);
    }
    public void envirEmailParaRestablecer(String email) {
        OlvidaPass olvidaPass = new OlvidaPass(email);
        ApiClient.Endpoints api = ApiClient.getApi();
        Call<ResponseBody> call = api.enviarEmail(olvidaPass);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Email enviado para restablecer el password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "Email no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplication(), "Error al enviar el email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
