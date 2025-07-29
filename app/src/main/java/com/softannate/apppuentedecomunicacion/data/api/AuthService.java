package com.softannate.apppuentedecomunicacion.data.api;

import com.google.gson.JsonObject;
import com.softannate.apppuentedecomunicacion.modelos.dto.TokenDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interfaz de servicio para operaciones de autenticación con Retrofit.
 * <p>
 * Define el endpoint para refrescar tokens de acceso utilizando un cuerpo JSON.
 * Se espera un objeto {@link TokenDto} como respuesta.
 */
public interface AuthService {

    /**
     * Solicita un nuevo access token utilizando un refresh token.
     * <p>
     * Este método realiza una petición POST a {@code /auth/refresh} enviando
     * el refresh token en el cuerpo del mensaje como un objeto JSON.
     *
     * @param refreshToken Objeto {@link JsonObject} que contiene el refresh token
     * @return Llamada Retrofit que retorna un {@link TokenDto} con el nuevo token
     */
    @POST("/auth/refresh")
    Call<TokenDto> refreshTokenSync(@Body JsonObject refreshToken);
}