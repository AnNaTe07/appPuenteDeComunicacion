package com.softannate.apppuentedecomunicacion.modelos;

import com.softannate.apppuentedecomunicacion.modelos.dto.UsuarioDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Mensaje implements Serializable {

    private int id;
    private String asunto;
    private String texto;
    private Date fecha;
    private int usuarioId;
    private UsuarioDto usuario;
    private int alumnoId;
    private Alumno alumno;
    private List<MensajeUsuario>receptores;

    public Mensaje(int id, String asunto, String texto, Date fecha, int usuarioId, UsuarioDto usuario, int alumnoId, Alumno alumno, List<MensajeUsuario> receptores) {
        this.id = id;
        this.asunto = asunto;
        this.texto = texto;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.alumno = alumno;
        this.alumnoId = alumnoId;
        this.receptores = receptores;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public List<MensajeUsuario> getReceptores() {
        return receptores;
    }

    public void setReceptores(List<MensajeUsuario> receptores) {
        this.receptores = receptores;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }
}
