package com.softannate.apppuentedecomunicacion.modelos.dto;

import com.softannate.apppuentedecomunicacion.modelos.Rol;

public class DestinatarioDto {
    private int id;
    private String nombre;
    private Rol rol;

    public DestinatarioDto(int id, String nombre, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rol=" + rol;
    }
}
