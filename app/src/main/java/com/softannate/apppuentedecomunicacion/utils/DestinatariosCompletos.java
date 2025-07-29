package com.softannate.apppuentedecomunicacion.utils;

import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import java.util.List;
import java.util.Map;

public class DestinatariosCompletos {
    public final List<DestinatarioDto> personal;
    public final List<CursoDto> cursos;
    public final Map<Integer, List<AlumnoDto>> alumnosPorCurso;

    public DestinatariosCompletos(List<DestinatarioDto> personal,
                                       List<CursoDto> cursos,
                                       Map<Integer, List<AlumnoDto>> alumnosPorCurso) {
        this.personal = personal;
        this.cursos = cursos;
        this.alumnosPorCurso = alumnosPorCurso;
    }
}

