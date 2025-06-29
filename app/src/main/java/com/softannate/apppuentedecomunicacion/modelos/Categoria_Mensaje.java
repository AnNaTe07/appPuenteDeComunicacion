package com.softannate.apppuentedecomunicacion.modelos;

public class Categoria_Mensaje {
    private int id;
    private String nombre;

    public Categoria_Mensaje(int id, String nombre) {
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

    @Override
    public String toString() {
        return  "id=" + id +
                ", nombre='" + nombre ;
    }
}
