package com.softannate.apppuentedecomunicacion.modelos.dto;

import java.util.List;

/**
 * DTO que encapsula un curso y su lista de alumnos.(AgendaViewModel)
 */
public class CursoConAlumnosDto {
    public final String nombreCurso;
    public final List<AlumnoDto> alumnos;

    public CursoConAlumnosDto(String nombreCurso, List<AlumnoDto> alumnos) {
        this.nombreCurso = nombreCurso;
        this.alumnos = alumnos;
    }
}
