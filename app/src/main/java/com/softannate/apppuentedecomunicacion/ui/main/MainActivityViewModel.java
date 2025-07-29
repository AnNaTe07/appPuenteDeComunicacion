package com.softannate.apppuentedecomunicacion.ui.main;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.messaging.FirebaseMessaging;
import com.softannate.apppuentedecomunicacion.base.ViewModelBase;
import com.softannate.apppuentedecomunicacion.modelos.dto.TokenFirebase;
import com.softannate.apppuentedecomunicacion.modelos.dto.UsuarioDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModelBase {

    private MutableLiveData<UsuarioDto> usuario;
    private MutableLiveData<String> avatar;
    private Context contexto;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.contexto = application;
        usuario = new MutableLiveData<>();
        avatar = new MutableLiveData<>();
    }

    public LiveData<UsuarioDto> getUsuario(){
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
    public void obtenerYEnviarTokenFcm() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "No se pudo obtener el token", task.getException());
                        return;
                    }

                    String token = task.getResult();

                    TokenFirebase dto = new TokenFirebase(token);

                    Call<Void> call = endpoints.actualizarToken(dto);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("FCM", "Token FCM enviado correctamente");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("FCM", " Error al enviar token", t);
                        }
                    });
                });
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

