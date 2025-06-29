package com.softannate.apppuentedecomunicacion.ui.main;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.apppuentedecomunicacion.modelos.Usuario;
import com.softannate.apppuentedecomunicacion.data.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Usuario> usuario;
    private MutableLiveData<String> avatar;
    private Context contexto;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.contexto = application;
        usuario = new MutableLiveData<>();
        avatar = new MutableLiveData<>();
    }

    public LiveData<Usuario> getUsuario(){
        if(usuario == null){
            usuario = new MutableLiveData<>();
        }
        return usuario;
    }

    public LiveData<String> getAvatar(){
        if(avatar == null){
            avatar = new MutableLiveData<>();
        }
        return avatar;
    }

/*
    public void obtenerUsuario(){
        ApiService.Endpoints api = ApiService.getApi();
        Call<Usuario> call = api.profile(ApiService.getToken(contexto));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful() && response.body() != null) {
                    usuario.setValue(response.body());
                    avatar.setValue(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable throwable) {
                Toast.makeText(contexto, "Error al mostrar los datos de usuario", Toast.LENGTH_SHORT).show();
            }
        });

 */

    }

