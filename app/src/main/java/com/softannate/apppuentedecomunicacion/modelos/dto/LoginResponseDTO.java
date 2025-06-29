package com.softannate.apppuentedecomunicacion.modelos.dto;

import androidx.annotation.NonNull;

import com.softannate.apppuentedecomunicacion.modelos.Rol;

public class LoginResponseDTO {

    private String token;
    private String expiracion_token;
    private String refresh_token;
    private String refresh_expiracion;
    private Rol rol;
    private String nombreCompleto;
    private String email;
    private int id;

    public LoginResponseDTO(String token, String expiracion_token, String refresh_token, String refresh_expiracion, Rol rol, String nombreCompleto, String email, int id) {
        this.token = token;
        this.expiracion_token = expiracion_token;
        this.refresh_token = refresh_token;
        this.refresh_expiracion = refresh_expiracion;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiracion_token() {
        return expiracion_token;
    }

    public void setExpiracion_token(String expiracion_token) {
        this.expiracion_token = expiracion_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_expiracion() {
        return refresh_expiracion;
    }

    public void setRefresh_expiracion(String refresh_expiracion) {
        this.refresh_expiracion = refresh_expiracion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginDtoRequest{" +
                "token='" + token + '\'' +
                ", expiracion_token='" + expiracion_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", refresh_expiracion='" + refresh_expiracion + '\'' +
                ", rol=" + rol +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                '}';
    }
}

