package com.softannate.apppuentedecomunicacion.ui.main.perfil.modificarPass;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.apppuentedecomunicacion.modelos.CambioPass;
import com.softannate.apppuentedecomunicacion.data.api.ApiService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModificarPassViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<CambioPass> mPass;
    private MutableLiveData<Boolean> redirigirLogin;

    public ModificarPassViewModel(@NonNull Application application) {
        super(application);
        mPass = new MutableLiveData<CambioPass>();
        redirigirLogin = new MutableLiveData<>();
        context = application.getApplicationContext();
    }

    public LiveData<CambioPass> getPass() {
        if (mPass == null) {
            mPass = new MutableLiveData<>();
        }
        return mPass;
    }

    public LiveData<Boolean> getRedirigirLogin() {
        return  redirigirLogin;
    }

    public void cambiarPass(String passActual, String nuevoPass, String confirmaPass) {
        if(passActual.isEmpty() || nuevoPass.isEmpty() || confirmaPass.isEmpty()) {
            mPass.setValue(null);
            Toast.makeText(context, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return;//sale si alguno de los campos está vacio
        }
        if(!nuevoPass.equals(confirmaPass)) {
            mPass.setValue(null);
            Toast.makeText(context, "'Nuevo Password' y 'Confirmar Nuevo Password' deben coincidir.", Toast.LENGTH_SHORT).show();
            return;//sale si los pass no coinciden
        }
        CambioPass pass = new CambioPass(passActual, nuevoPass);
        /*
        ApiService.Endpoints api = ApiService.getApi();
        String token = ApiService.getToken(context);

        Call<ResponseBody> call = api.cambiarPass(token, pass);
        Log.d("Pass", "actual: " + pass.getPassActual() + ", nuevo: " + pass.getNuevoPass());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    mPass.setValue(new CambioPass(passActual, nuevoPass));
                    Toast.makeText(context, "Password cambiada correctamente", Toast.LENGTH_SHORT).show();
                    redirigirLogin.setValue(true);//redirijo al login al realizar el cambio
                }else {
                    Log.d("Error", "Código de error: " + response.code());
                    Toast.makeText(getApplication(), "Error al intentar cambiar el Password.", Toast.LENGTH_SHORT).show();
                    try {
                        String error = response.errorBody().string();
                        Log.d("Error", error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPass.setValue(null);//indico error
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(context, "Error al cambiar el password. Por favor verifica tu conexión.", Toast.LENGTH_SHORT).show();
                mPass.setValue(null);//indico error
                redirigirLogin.setValue(false);
            }
        });

         */
    }
}