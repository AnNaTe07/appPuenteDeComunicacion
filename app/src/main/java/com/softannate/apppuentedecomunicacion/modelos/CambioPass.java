package com.softannate.apppuentedecomunicacion.modelos;

public class CambioPass {

    private String passActual;
    private String nuevoPass;

    public CambioPass(String passActual, String nuevoPass) {
        this.passActual = passActual;
        this.nuevoPass = nuevoPass;
    }

    public String getPassActual() {
        return passActual;
    }

    public void setPassActual(String passActual) {
        this.passActual = passActual;
    }

    public String getNuevoPass() {
        return nuevoPass;
    }

    public void setNuevoPass(String nuevoPass) {
        this.nuevoPass = nuevoPass;
    }
}
