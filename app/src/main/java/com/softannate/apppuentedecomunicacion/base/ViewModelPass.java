package com.softannate.apppuentedecomunicacion.base;

import android.app.Application;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.softannate.apppuentedecomunicacion.utils.SingleLiveEvent;

public abstract class ViewModelPass extends ViewModelBase {

    protected SingleLiveEvent<Boolean> redirigirLogin = new SingleLiveEvent<>();

    public ViewModelPass(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getRedirigirLogin() {
        if (redirigirLogin == null) {
            redirigirLogin = new SingleLiveEvent<>();
        }
        return redirigirLogin;
    }

    public void passIguales(String pass, String confirmarPass) {
        if(!pass.equals(confirmarPass)) {
            mensaje.setValue("Las contraseñas no coinciden");
            return;
        }
    }

    public void camposVacios(String pass, String nuevoPass, @Nullable String confirmarPass) {
        if (pass.isEmpty() || nuevoPass.isEmpty()) {
            mensaje.setValue("Todos los campos son obligatorios");
            return;
        }

        if (confirmarPass != null && confirmarPass.isEmpty()) {
            mensaje.setValue("Todos los campos son obligatorios");
        }
    }

    public boolean validarEmail(String email){
        if(email.isEmpty()){
            mensaje.setValue(("Por favor, ingrese su email"));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensaje.setValue("Formato de email inválido");
            return false;
        }
        return true;
    }
}
