package com.softannate.apppuentedecomunicacion.modelos;

public class MensajeUsuario {

    private int mensajeId;
    private Mensaje mensaje;
    private int usuarioId;
    private UsuarioDto usuario;
    private int alumnoId;
    private Alumno alumno;

    public MensajeUsuario(int mensajeId, Mensaje mensaje, int usuarioId, UsuarioDto usuario, int alumnoId, Alumno alumno) {
        this.mensajeId = mensajeId;
        this.mensaje = mensaje;
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.alumnoId = alumnoId;
        this.alumno = alumno;
    }

    public int getMensajeId() {
        return mensajeId;
    }

    public void setMensajeId(int mensajeId) {
        this.mensajeId = mensajeId;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
}
