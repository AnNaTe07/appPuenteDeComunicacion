package com.softannate.apppuentedecomunicacion.ui.login.restablecerPass;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.apppuentedecomunicacion.modelos.RestablecerPass;
import com.softannate.apppuentedecomunicacion.data.api.ApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestablecerPassActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> passValido; //si el pass es valido
    private MutableLiveData<String> mensaje;
    private String email;
    private String token;
    private MutableLiveData<Boolean> redirigirLogin;

    public RestablecerPassActivityViewModel(@NonNull Application application) {
        super(application);
        passValido = new MutableLiveData<>();
        mensaje = new MutableLiveData<>();
        redirigirLogin = new MutableLiveData<>();
    }

    //para obtener validez del pass
    public LiveData<Boolean> getPassValido() {
        if(passValido == null) {
            passValido = new MutableLiveData<>();
        }
        return passValido;
    }

    //para obtener el mensaje de error
    public LiveData<String> getMensaje() {
        if(mensaje == null) {
            mensaje = new MutableLiveData<>();
        }
        return mensaje;
    }

    //para cerrar el activity
    public LiveData<Boolean> getRedirigirLogin() {
        if(redirigirLogin == null) {
            redirigirLogin = new MutableLiveData<>();
        }
        return redirigirLogin;
    }

    //para establecer el email
    public void setEmail(String email) {
        this.email = email;//seteo el email recibido
    }

    //para obtener email almacenado
    public String getEmail() {
        return email;
    }

    //para establecer el token
    public void setToken(String token) {
        this.token = token;//seteo el token recibido
    }

    //para obtener token almacenado
    public String getToken() {
        return token;
    }

    //para validar y restablecer el pass
    public void validarYRestablecer(String nuevaPass, String confirmarPass) {
        if(nuevaPass.isEmpty() || confirmarPass.isEmpty()) {
            mensaje.setValue("Todos los campos son obligatorios");
            passValido.setValue(false);//si alguno de los campos está vacio, no es valido
            return;//sale si alguno de los campos está vacio
        } else if (!nuevaPass.equals(confirmarPass)) {
            mensaje.setValue("'Password' y 'Confirmar Password' deben coincidir.");
            passValido.setValue(false);//si los pass no coinciden, no es valido
            return;//sale si los pass no coinciden
        }
        //si los pass son validos llamo a restablecerPass
        passValido.setValue(true);//si son validos
       // restablecerPass(email, token, nuevaPass);
    }
    //para restablecer el pass
    /*
    public void restablecerPass(String email, String token, String nuevaPass) {
        String token2 = "Bearer " + token;
        Log.d("RestablecerPass", "Token: " + token2);
        RestablecerPass dto = new RestablecerPass(token, email, nuevaPass);//dto para enviar
        Log.d("RestablecerPass", "DTO: " + dto);

        ApiService.Endpoints api = ApiService.getApi();
        Call<ResponseBody> call = api.restablecerPass(dto, token2);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplication(), "Password restablecido correctamente", Toast.LENGTH_SHORT).show();
                    redirigirLogin.setValue(true);//redirijo al login al realizar el cambio
                } else {
                    Log.e("RestablecerPass", "Fallo en la solicitud: " + response.message());
                    Toast.makeText(getApplication(), "Error al intentar restablecer el Password.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(getApplication(), "Error al intentar restablecer el Password. Fallo en la solicitud.", Toast.LENGTH_SHORT).show();
                Log.e("RestablecerPass", "Fallo en la solicitud: " + throwable.getMessage());
            }
        });
    }

     */

}
