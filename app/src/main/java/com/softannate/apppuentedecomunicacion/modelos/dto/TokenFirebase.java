package com.softannate.apppuentedecomunicacion.modelos.dto;

public class TokenFirebase {

    private String token;

    public TokenFirebase( String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}