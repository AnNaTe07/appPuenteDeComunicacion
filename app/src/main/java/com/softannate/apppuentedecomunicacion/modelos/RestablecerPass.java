package com.softannate.apppuentedecomunicacion.modelos;

public class RestablecerPass {

    public String token;
    public String email;
    public String nuevoPass;

    public RestablecerPass(String token, String email, String nuevoPass) {
        this.token = token;
        this.email = email;
        this.nuevoPass = nuevoPass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaPass() {
        return nuevoPass;
    }

    public void setNuevaPass(String nuevaPass) {
        this.nuevoPass = nuevaPass;
    }
}
