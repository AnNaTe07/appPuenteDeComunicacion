package com.softannate.apppuentedecomunicacion.ui.main.perfil;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
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
import com.softannate.apppuentedecomunicacion.data.api.ApiService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> avatar;
    private MutableLiveData<Usuario> musuario;
    private MutableLiveData<Boolean> editar = new MutableLiveData<>(false);
    private MutableLiveData<String> btn = new MutableLiveData<>("Editar");
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

    /*
        public void obtenerUsuario() {
            ApiService.Endpoints api = ApiService.getApi();
            Call<Usuario> call = api.profile(ApiService.getToken(context));

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

        public void editarPerfil(Usuario usuario) {
            // Verifico que la fecha de nacimiento no esté vacía o sea null
            if (usuario.getFechaNacimiento() == null || usuario.getFechaNacimiento().isEmpty()) {
                Log.e("FechaInput(VIEW)", "Fecha de nacimiento no válida.");
                Toast.makeText(context, "Fecha de nacimiento no válida.", Toast.LENGTH_SHORT).show();
                return; // Salir de la función si la fecha está vacía
            }
            ApiService.Endpoints api = ApiService.getApi();

            // Creo un usuario con los datos
            String email = usuario.getEmail();
            Usuario actualizado = new Usuario();
            actualizado.setApellido(usuario.getApellido());
            actualizado.setEmail(email);
            actualizado.setNombre(usuario.getNombre());
            actualizado.setTelefono(usuario.getTelefono());
            actualizado.setDni(usuario.getDni());
            actualizado.setDomicilio(usuario.getDomicilio());
            actualizado.setFechaNacimiento(usuario.getFechaNacimiento());
            actualizado.setRolId(usuario.getRolId());

            // llamada a la API para actualizar el usuario
            Call<Usuario> call = api.update(ApiService.getToken(context), actualizado);
            Log.d("Datos a actualizar", "apellido: " + actualizado.getApellido() + ", nombre: " + actualizado.getNombre() +
                    ", telefono: " + actualizado.getTelefono() + ", dni: " + actualizado.getDni() + ", email: " + actualizado.getEmail() +
                    ", rolId: " + actualizado.getRolId() + " fecha: " + actualizado.getFechaNacimiento());

            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful() && response.body() != null) {musuario.setValue(response.body()); // Actualizo datos
                        obtenerUsuario();  // Recargo datos del usuario
                    } else {
                        Log.d("Error API", "Cuerpo de la respuesta: " + response.errorBody().toString());
                    }
                }
                @Override
                public void onFailure(Call<Usuario> call, Throwable throwable) {
                    Toast.makeText(context, "Error al actualizar los Datos", Toast.LENGTH_SHORT).show();
                }
            });
        }


     */
    public void cambioEditText(ViewGroup layout, boolean editable) {

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof EditText) {
                child.setFocusable(editable);
                child.setFocusableInTouchMode(editable);
                child.setEnabled(editable);
            }
        }
    }
/*
    public void subirAvatar(Uri uri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            File file = new File(context.getCacheDir(), "avatar.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part avatarFoto = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

 */
/*
            ApiService.Endpoints api = ApiService.getApi();
            String token = ApiService.getToken(context);

            Call<ResponseBody> llamadaASubir = api.updateAvatar(token, avatarFoto);
            llamadaASubir.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            // Asigno la URL
                            avatar.setValue(response.body().string());
                            obtenerUsuario();
                            Toast.makeText(context, "Foto de perfil actualizada correctamente", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d("error", response.code() + " - " + response.message());
                        Toast.makeText(context, "Error al subir la foto de perfil: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.d("errorAvatar", throwable.getMessage());
                    Toast.makeText(context, "Fallo al subir el avatar", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("errorAvatar", e.getMessage());
            Toast.makeText(context, "Error al obtener el archivo de la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarAvatar() {

            ApiService.Endpoints api = ApiService.getApi();
            String token = ApiService.getToken(getApplication());
            Call<String> llamadaAEliminar = api.deleteAvatar(token);
            llamadaAEliminar.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        avatar.setValue("");
                        obtenerUsuario();
                        Toast.makeText(getApplication(), "Avatar eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("error", response.code() + " - " + response.message());
                        Toast.makeText(getApplication(), "Error al eliminar el avatar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {
                    Log.d("errorEliminarAvatar", throwable.getMessage());
                }
            });

 */
        }



