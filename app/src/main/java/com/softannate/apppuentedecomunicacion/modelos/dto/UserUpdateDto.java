package com.softannate.apppuentedecomunicacion.modelos.dto;

public class UserUpdateDto {
    private String telefono;
    private String domicilio;
    private String fechaNacimiento;

    public UserUpdateDto(String telefono, String domicilio, String fechaNacimiento) {
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
