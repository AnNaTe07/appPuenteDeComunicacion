package com.softannate.apppuentedecomunicacion.base;

import android.app.Application;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.softannate.apppuentedecomunicacion.utils.SingleLiveEvent;

/**
 * ViewModel base para operaciones relacionadas con validación de credenciales
 * y redirección de login. Extiende de {@link ViewModelBase} para utilizar
 * mecanismos de comunicación como mensajes y LiveData compartidos.
 */
public abstract class ViewModelPass extends ViewModelBase {

    /** Evento para notificar que se debe redirigir al login */
    protected SingleLiveEvent<Boolean> redirigirLogin = new SingleLiveEvent<>();

    /**
     * Constructor de la clase ViewModelPass.
     *
     * @param application instancia de {@link Application}, requerida para ViewModelBase
     */
    public ViewModelPass(@NonNull Application application) {
        super(application);
    }

    /**
     * Devuelve el LiveData asociado al evento de redirección de login.
     * <p>Si no ha sido inicializado, se crea una nueva instancia de {@link SingleLiveEvent}.
     *
     * @return LiveData que emite valores booleanos para redirigir al login
     */
    public LiveData<Boolean> getRedirigirLogin() {
        if (redirigirLogin == null) {
            redirigirLogin = new SingleLiveEvent<>();
        }
        return redirigirLogin;
    }

    /**
     * Valida si dos contraseñas ingresadas son iguales.
     * <p>En caso de discrepancia, emite un mensaje indicando el error.
     *
     * @param pass           primera contraseña
     * @param confirmarPass  segunda contraseña para confirmar
     */
    public void passIguales(String pass, String confirmarPass) {
        if(!pass.equals(confirmarPass)) {
            mensaje.setValue("Las contraseñas no coinciden");
            return;
        }
    }

    /**
     * Verifica que los campos de contraseña no estén vacíos.
     * <p>En caso de encontrar algún campo vacío, emite un mensaje de error.
     *
     * @param pass          contraseña actual
     * @param nuevoPass     nueva contraseña
     * @param confirmarPass confirmación de nueva contraseña (puede ser null)
     */
    public void camposVacios(String pass, String nuevoPass, @Nullable String confirmarPass) {
        if (pass.isEmpty() || nuevoPass.isEmpty()) {
            mensaje.setValue("Todos los campos son obligatorios");
            return;
        }

        if (confirmarPass != null && confirmarPass.isEmpty()) {
            mensaje.setValue("Todos los campos son obligatorios");
        }
    }

    /**
     * Valida el formato de un email ingresado.
     * <p>Si el campo está vacío o no cumple con el patrón estándar,
     * emite un mensaje de error.
     *
     * @param email email a validar
     * @return true si el email es válido, false en caso contrario
     */
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
