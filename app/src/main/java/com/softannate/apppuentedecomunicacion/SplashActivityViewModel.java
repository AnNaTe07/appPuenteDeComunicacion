package com.softannate.apppuentedecomunicacion;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.modelos.Navegacion;
import com.softannate.apppuentedecomunicacion.request.ApiClient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SplashActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Navegacion> navegacion = new MutableLiveData<>();
    public LiveData<Navegacion> nav=navegacion;

    public SplashActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public  void estadoUsuario(){

        //recupero token y expiracion
        String token = ApiClient.getToken(getApplication());
        String expiracion = ApiClient.getExpiracion(getApplication());
        Log.d("SplashActivity", "Token: " + token);  // Verifico el token
        Log.d("SplashActivity", "Expiración: " + expiracion);  // Verifico la expiración


        if(token ==null || expiracion == null){
            navegacion.setValue(Navegacion.LOGIN);
            return;
        }


        if(isTokenvalido(expiracion)){
            navegacion.setValue(Navegacion.INICIO);
        }else{
            navegacion.setValue(Navegacion.LOGIN);
        }
    }
    private boolean isTokenvalido(String expiracion) {
        try{
            //convierto la fecha de string  Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date fechaexpiracion = sdf.parse(expiracion);
            if(fechaexpiracion!= null){
            //comparo fecha expiracion con fecha actual
                return fechaexpiracion.after(new Date());
            }
        }catch(ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
