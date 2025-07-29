package com.softannate.apppuentedecomunicacion.modelos.dto;

import android.net.Uri;

import java.util.List;

public class MensajeTutorDto {

    private String mensaje;
    private int idCategoria;
    private int idAlumno;
    private List<Integer> idUsuarios;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public List<Integer> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(List<Integer> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }


    @Override
    public String toString() {
        return  "mensaje='" + mensaje + '\'' +
                ", idCategoria=" + idCategoria +
                ", idAlumno=" + idAlumno +
                ", idUsuarios=" + idUsuarios;
    }
}
