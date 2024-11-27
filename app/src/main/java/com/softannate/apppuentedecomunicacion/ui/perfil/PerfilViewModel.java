package com.softannate.apppuentedecomunicacion.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.modelos.Usuario;
import com.softannate.apppuentedecomunicacion.request.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> avatar;
    private MutableLiveData<Usuario> musuario;
    private MutableLiveData<Boolean> editar;
    private MutableLiveData<String> btn;
    private String email;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        this.avatar = new MutableLiveData<>("");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public LiveData<Usuario> getMUsuario() {
        if (musuario == null) {
            musuario = new MutableLiveData<>();
        }
        return musuario;
    }

    public LiveData<String> getAvatar() {
        if (avatar == null) {
            avatar = new MutableLiveData<>();
        }
        return avatar;
    }

    public LiveData<Boolean> getEditar() {
        if (editar == null) {
            editar = new MutableLiveData<>();
        }
        return editar;
    }

    public LiveData<String> getBtn() {
        if (btn == null) {
            btn = new MutableLiveData<>();
        }
        return btn;
    }

    public void obtenerUsuario() {
        ApiClient.Endpoints api = ApiClient.getApi();
        Call<Usuario> call = api.profile(ApiClient.getToken(context));

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    musuario.setValue(response.body());
                    avatar.setValue(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable throwable) {
                Toast.makeText(context, "Error al mostrar los datos de usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void cambioBoton() {
        if (editar.getValue() != null && editar.getValue()) {
            editar.setValue(true);
            editar.setValue(false);
            btn.setValue("Editar");
            obtenerUsuario();
        } else {
            editar.setValue(true);//activo modo edicion
            btn.setValue("Guardar");
        }
    }
    public void cambioEditText(ViewGroup layout){
        boolean editable= editar.getValue() != null && editar.getValue();
        for(int i =0; i<layout.getChildCount();i++){
            View child= layout.getChildAt(i);
            if(child instanceof EditText){
                child.setFocusable(editable);
                child.setFocusableInTouchMode(editable);
                child.setEnabled(editable);
            }
        }
    }

}
