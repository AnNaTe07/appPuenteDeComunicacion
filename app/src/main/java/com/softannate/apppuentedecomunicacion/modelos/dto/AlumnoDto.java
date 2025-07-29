package com.softannate.apppuentedecomunicacion.modelos.dto;

import java.util.List;

public class AlumnoDto {
    private int id;
    private String nombre;
    private List<String> telefonos;
    private boolean seleccionado;


    public AlumnoDto() {
    }

    public AlumnoDto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public AlumnoDto(int id, String nombre, List<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = telefonos;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<String> telefonos) {
        telefonos = telefonos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", nombre='" + nombre +
                "telefonos=" + telefonos;

    }
}
