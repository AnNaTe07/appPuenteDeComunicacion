package com.softannate.apppuentedecomunicacion.modelos.dto;

import com.softannate.apppuentedecomunicacion.modelos.Archivo_adjunto;
import java.io.Serializable;
import java.util.List;

public class MensajeDTO implements Serializable {
    private int mensajeId;
    private String contenido;
    private String fecha_Hora;
    private int emisorId;
    private String emisorNombre;
    private int alumnoId;
    private String alumnoNombre;
    private int receptorId;
    private String receptorNombre;
    private int estadoId;
    private String categoria;
    private int mensajesNoLeidos;
    private String avatarE;
    private String avatarR;
    private List<Archivo_adjunto> archivos;


    public MensajeDTO(int mensajeId, String contenido, String fecha_Hora, int emisorId, String emisorNombre, int alumnoId, String alumnoNombre, int receptorId, String receptorNombre, int estadoId, String categoria, int mensajesNoLeidos, String avatarE, String avatarR, List<Archivo_adjunto> archivos) {
        this.mensajeId = mensajeId;
        this.contenido = contenido;
        this.fecha_Hora = fecha_Hora;
        this.emisorId = emisorId;
        this.emisorNombre = emisorNombre;
        this.alumnoId = alumnoId;
        this.alumnoNombre = alumnoNombre;
        this.receptorId = receptorId;
        this.receptorNombre = receptorNombre;
        this.estadoId = estadoId;
        this.categoria = categoria;
        this.mensajesNoLeidos = mensajesNoLeidos;
        this.avatarE = avatarE;
        this.avatarR = avatarR;
        this.archivos = archivos;
    }

    public int getMensajeId() {
        return mensajeId;
    }

    public void setMensajeId(int mensajeId) {
        this.mensajeId = mensajeId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha_Hora() {
        return fecha_Hora;
    }

    public void setFecha_Hora(String fecha_Hora) {
        this.fecha_Hora = fecha_Hora;
    }

    public int getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(int emisorId) {
        this.emisorId = emisorId;
    }

    public String getEmisorNombre() {
        return emisorNombre;
    }

    public void setEmisorNombre(String emisorNombre) {
        this.emisorNombre = emisorNombre;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public int getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(int receptorId) {
        this.receptorId = receptorId;
    }

    public String getReceptorNombre() {
        return receptorNombre;
    }

    public void setReceptorNombre(String receptorNombre) {
        this.receptorNombre = receptorNombre;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getMensajesNoLeido() {
        return mensajesNoLeidos;
    }

    public void setMensajesNoLeido(int noLeido) {
        this.mensajesNoLeidos = noLeido;
    }

    public String getAvatar() {
        return avatarE;
    }

    public void setAvatar(String avatar) {
        this.avatarE = avatar;
    }

    public String getAvatarR() {
        return avatarR;
    }

    public void setAvatarR(String avatar) {
        this.avatarR = avatar;
    }

    public List<Archivo_adjunto> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo_adjunto> archivos) {
        this.archivos = archivos;
    }


    @Override
    public String toString() {
        return  "mensajeId=" + mensajeId +
                ", contenido='" + contenido + '\'' +
                ", fecha_Hora='" + fecha_Hora + '\'' +
                ", emisorId=" + emisorId +
                ", emisorNombre='" + emisorNombre + '\'' +
                ", alumnoId=" + alumnoId +
                ", alumnoNombre='" + alumnoNombre + '\'' +
                ", receptorId='" + receptorId + '\'' +
                ", receptorNombre='" + receptorNombre + '\'' +
                ", estadoId=" + estadoId +'\'' +
                ", categoria='" + categoria + '\'' +
                ", mensajesNoLeido=" + mensajesNoLeidos + '\'' +
                ", avatarE='" + avatarE + '\'' +
                ", avatarR='" + avatarR + '\'';
    }
}