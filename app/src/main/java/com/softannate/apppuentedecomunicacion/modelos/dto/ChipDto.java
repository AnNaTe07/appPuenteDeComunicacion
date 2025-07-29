package com.softannate.apppuentedecomunicacion.modelos.dto;

public class ChipDto {

        private final int id;
        private final String nombre;
        private final String tipo; // "personal", "curso", "alumno"

        public ChipDto(int id, String nombre, String tipo) {
            this.id = id;
            this.nombre = nombre;
            this.tipo = tipo;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getTipo() { return tipo; }

}
