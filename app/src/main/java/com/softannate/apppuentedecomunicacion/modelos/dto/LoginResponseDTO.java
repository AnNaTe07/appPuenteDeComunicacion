package com.softannate.apppuentedecomunicacion.modelos.dto;

import androidx.annotation.NonNull;
import com.softannate.apppuentedecomunicacion.modelos.Rol;

/**
 * DTO que representa la respuesta de inicio de sesión.
 * <p>
 * Contiene información del token JWT principal, token de refresh,
 * expiraciones asociadas, datos del usuario autenticado y su rol.
 */
public class LoginResponseDTO {

    private String token;
    private String expiracion_token;
    private String refresh_token;
    private String refresh_expiracion;
    private Rol rol;
    private String nombreCompleto;
    private String email;
    private int id;

    /**
     * Constructor que inicializa todos los campos de la respuesta de login.
     *
     * @param token               token JWT generado por el servidor
     * @param expiracion_token   fecha de expiración del token principal
     * @param refresh_token      token utilizado para renovar la sesión
     * @param refresh_expiracion fecha de expiración del refresh token
     * @param rol                 rol del usuario autenticado
     * @param nombreCompleto     nombre completo del usuario
     * @param email              email del usuario
     * @param id                 identificador único del usuario
     */
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

    // Métodos getters y setters

    /** @return token JWT generado por el servidor */
    public String getToken() {
        return token;
    }

    /** @param token nuevo token JWT */
    public void setToken(String token) {
        this.token = token;
    }

    /** @return fecha de expiración del token principal */
    public String getExpiracion_token() {
        return expiracion_token;
    }

    /** @param expiracion_token nueva fecha de expiración del token */
    public void setExpiracion_token(String expiracion_token) {
        this.expiracion_token = expiracion_token;
    }

    /** @return token de renovación de sesión */
    public String getRefresh_token() {
        return refresh_token;
    }

    /** @param refresh_token nuevo token de renovación */
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    /** @return fecha de expiración del refresh token */
    public String getRefresh_expiracion() {
        return refresh_expiracion;
    }

    /** @param refresh_expiracion nueva fecha de expiración del refresh token */
    public void setRefresh_expiracion(String refresh_expiracion) {
        this.refresh_expiracion = refresh_expiracion;
    }

    /** @return rol del usuario (por ejemplo: ADMIN, USER, etc.) */
    public Rol getRol() {
        return rol;
    }

    /** @param rol nuevo rol a asignar al usuario */
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /** @return nombre completo del usuario */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /** @param nombreCompleto nuevo nombre completo */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /** @return dirección de correo electrónico del usuario */
    public String getEmail() {
        return email;
    }

    /** @param email nuevo email del usuario */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return ID numérico único del usuario */
    public int getId() {
        return id;
    }

    /** @param id nuevo identificador de usuario */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Representación en texto del objeto, útil para logging y debugging.
     *
     * @return string con todos los valores del DTO
     */
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

