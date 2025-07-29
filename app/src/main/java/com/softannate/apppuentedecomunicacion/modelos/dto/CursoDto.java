package com.softannate.apppuentedecomunicacion.modelos.dto;

public class CursoDto {
    private int id;
    private String nombre;
    private boolean seleccionado;

    public CursoDto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
                ", nombre='" + nombre ;
    }
}
