package com.softannate.apppuentedecomunicacion.modelos;

import java.io.Serializable;

public class Archivo_adjunto implements Serializable {

        private int id;
        private int mensajeId;
        private String nombre;
        private String url;
        private String tipo_Mime;
        private Integer tamaño;      //Integer para permitir null
        private String fecha_hora;

        //  Constructor vacío (requerido por deserializadores)
        public Archivo_adjunto() {}

        //  Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getMensajeId() {
            return mensajeId;
        }

        public void setMensajeId(int mensajeId) {
            this.mensajeId = mensajeId;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTipoMime() {
            return tipo_Mime;
        }

        public void setTipoMime(String tipo_mime) {
            this.tipo_Mime = tipo_mime;
        }

        public Integer getTamaño() {
            return tamaño;
        }

        public void setTamaño(Integer tamaño) {
            this.tamaño = tamaño;
        }

        public String getFechaHora() {
            return fecha_hora;
        }

        public void setFechaHora(String fechaHora) {
            this.fecha_hora = fechaHora;
        }
}
