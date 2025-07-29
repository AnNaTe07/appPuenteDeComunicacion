package com.softannate.apppuentedecomunicacion.modelos.dto;

/**
 * DTO (Data Transfer Object) para encapsular los tokens de autenticación.
 * <p>
 * Contiene el access token y el refresh token utilizados en el proceso de autenticación
 * y renovación de credenciales.
 */
public class TokenDto {

    private String accessToken;
    private String refreshToken;

    /**
     * Obtiene el access token actual.
     *
     * @return cadena del access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Establece el access token.
     *
     * @param accessToken nuevo valor del access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Obtiene el refresh token actual.
     *
     * @return cadena del refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Establece el refresh token.
     *
     * @param refreshToken nuevo valor del refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Representación en cadena del objeto, útil para logging y depuración.
     *
     * @return descripción textual de los tokens
     */
    @Override
    public String toString() {
        return "TokenDto{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
