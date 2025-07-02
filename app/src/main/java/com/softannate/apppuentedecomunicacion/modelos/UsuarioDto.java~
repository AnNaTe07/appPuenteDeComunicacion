package com.softannate.apppuentedecomunicacion.modelos;


public class UsuarioDto {
    private int id;
    private String nombre;
    private String apellido;
    private String documento;
    private String email;
    private String telefono;
    private String domicilio;
    private String fechaNacimiento;
    private int rolId;
    private String rolNombre;
    private String avatar;
    private String nombreCompleto= nombre + " " + apellido;

    public UsuarioDto() {
    }

    public UsuarioDto(int id, String nombre, String apellido, String documento, String email, String telefono, String domicilio, String fechaNacimiento, int rolId, String rolNombre, String avatar) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.fechaNacimiento = fechaNacimiento;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
        this.avatar = avatar;
        this.nombreCompleto = this.nombre + " " + this.apellido;
    }


    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return  nombreCompleto;
    }

}
