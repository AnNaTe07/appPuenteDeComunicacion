package com.softannate.apppuentedecomunicacion.ui.main.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.ViewModelBase;
import com.softannate.apppuentedecomunicacion.modelos.UsuarioDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.PreferenciaNotificacionDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.UserUpdateDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends ViewModelBase {

    private MutableLiveData<String> avatar;
    private MutableLiveData<UsuarioDto> musuario;
    private MutableLiveData<Boolean> editar = new MutableLiveData<>(false);
    private MutableLiveData<String> btn = new MutableLiveData<>("Editar");
    private String email;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        this.avatar = new MutableLiveData<>("");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public LiveData<UsuarioDto> getMUsuario() {
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

    public void setEditar(boolean valor) {
        editar.setValue(valor); }

    public LiveData<String> getBtn() {
        if (btn == null) {
            btn = new MutableLiveData<>();
        }
        return btn;
    }


        public void obtenerUsuario() {
            Call<UsuarioDto> call = endpoints.profile();

            call.enqueue(new Callback<UsuarioDto>() {
                @Override
                public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        musuario.setValue(response.body());
                        avatar.setValue(response.body().toString());
                    }
                }
                @Override
                public void onFailure(Call<UsuarioDto> call, Throwable throwable) {
                    mensaje.setValue("Error al mostrar los datos de usuario");
                }
            });
        }

        public void editarPerfil(UserUpdateDto usuario) {
            if (usuario.getFechaNacimiento() == null || usuario.getFechaNacimiento().isEmpty()) {
                Log.e("FechaInput(VIEW)", "Fecha de nacimiento no válida.");
               mensaje.setValue("Fecha de nacimiento no válida.");
                return;
            }
            Call<UsuarioDto> call = endpoints.update(usuario);
            call.enqueue(new Callback<UsuarioDto>() {
                @Override
                public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        musuario.setValue(response.body());
                        mensaje.setValue("Datos actualizados correctamete");
                    } else {
                        mensaje.setValue("Error al actualizar los Datos");
                        Log.d("Error API", "Cuerpo de la respuesta: " + response.errorBody().toString());
                    }
                }
                @Override
                public void onFailure(Call<UsuarioDto> call, Throwable throwable) {
                    mensaje.setValue("Error al actualizar los Datos");
                }
            });
        }
    public void notificacion(int cb, boolean checked) {
        Log.d("respuesta1", "email: " + cb);
        PreferenciaNotificacionDto dto= new PreferenciaNotificacionDto(cb, checked);
        Call<Void> call = endpoints.actualizarNotificacion(dto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mensaje.setValue("Notificacion por email actualizada");
                    Log.d("respuesta1", "onResponse: " + response.code());
                } else {
                    Log.e("respuesta1", "Error al obtener la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("respuesta1", "Error al obtener la respuesta: " + throwable.getMessage());
            }
        });
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



