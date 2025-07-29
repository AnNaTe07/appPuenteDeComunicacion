package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.educacion;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.CursoDto;
import com.softannate.apppuentedecomunicacion.modelos.dto.DestinatarioDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter que gestiona una lista multisección para selección de destinatarios:
 * - Personal educativo
 * - Cursos
 * - Alumnos por curso
 *
 * Permite expandir/seccionar cada bloque, realizar selección individual o masiva
 * y sincroniza los cambios con un ViewModel u otro observador externo.
 *
 * Estructura interna:
 * - 'items': lista mixta con headers (String) y entidades (Dto).
 * - 'personalList': lista de docentes.
 * - 'cursoList': lista de cursos.
 * - 'alumnosPorCurso': mapa con alumnos agrupados por curso.
 */
public class DestinatarioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_HEADER = 0;
    private static final int TIPO_PERSONAL = 1;
    private static final int TIPO_CURSO = 2;
    private static final int TIPO_ALUMNO = 3;

    private final Context context;
    private final List<Object> items = new ArrayList<>();

    private boolean expandirPersonal = false;
    private boolean expandirCursos = false;

    private final List<DestinatarioDto> personalList = new ArrayList<>();
    private final List<CursoDto> cursoList = new ArrayList<>();
    private final Map<Integer, List<AlumnoDto>> alumnosPorCurso = new HashMap<>();
    private final Set<Integer> cursosExpandidos = new HashSet<>();

    /**
     * Callback que se dispara al expandir un curso.
     * Permite cargar dinámicamente los alumnos desde el ViewModel u origen externo.
     */
    public interface OnCursoExpandirListener {
        void cargarAlumnosDeCurso(int cursoId);
    }

    private OnCursoExpandirListener expandirListener;

    /**
     * Asigna el listener para manejar expansión de cursos.
     */
    public void setOnCursoExpandirListener(OnCursoExpandirListener listener) {
        this.expandirListener = listener;
    }

    /**
     * Callback invocado cada vez que cambia la selección.
     * Entrega listas actualizadas de los elementos seleccionados.
     */
    public interface OnSeleccionCambioListener {
        void onSeleccionCambio(List<DestinatarioDto> personal, List<CursoDto> cursos, List<AlumnoDto> alumnos);
    }

    private OnSeleccionCambioListener listener;

    /**
     * Constructor del adapter. Inicializa el contexto y construye la lista inicial visual.
     */
    public DestinatarioAdapter(Context context) {
        this.context = context;
        construirListaVisual();
    }

    /**
     * Asigna el listener que recibe los cambios de selección.
     */
    public void setOnSeleccionCambioListener(OnSeleccionCambioListener listener) {
        this.listener = listener;
    }

    /**
     * Recibe nuevas listas de datos y reconstruye la vista.
     *
     * @param personal lista de docentes
     * @param cursos lista de cursos
     * @param alumnosPorCurso mapa que relaciona ID de curso con sus alumnos
     */
    public void setData(List<DestinatarioDto> personal, List<CursoDto> cursos, Map<Integer, List<AlumnoDto>> alumnosPorCurso) {
        this.personalList.clear();
        this.cursoList.clear();
        this.alumnosPorCurso.clear();

        if (personal != null) this.personalList.addAll(personal);
        if (cursos != null) this.cursoList.addAll(cursos);
        if (alumnosPorCurso != null) this.alumnosPorCurso.putAll(alumnosPorCurso);

        construirListaVisual();
    }

    /**
     * Reconstruye visualmente la lista (headers, cursos, alumnos).
     * Se invoca ante cambios de selección o expansión.
     */
    private void construirListaVisual() {
        items.clear();

        items.add("📘 Personal educativo");
        if (expandirPersonal) items.addAll(personalList);

        items.add("🏫 Cursos");

        for (CursoDto curso : cursoList) {
            if (expandirCursos) items.add(curso);

            if (expandirCursos && cursosExpandidos.contains(curso.getId())) {
                List<AlumnoDto> alumnos = alumnosPorCurso.get(curso.getId());
                if (alumnos != null) items.addAll(alumnos);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Determina el tipo de vista (layout) a utilizar para el ítem en la posición dada.
     * Los tipos posibles están definidos como constantes: TIPO_HEADER, TIPO_PERSONAL, etc.
     *
     * @param position posición en la lista visual
     * @return entero que representa el tipo de vista
     */
    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) return TIPO_HEADER;
        if (item instanceof DestinatarioDto) return TIPO_PERSONAL;
        if (item instanceof CursoDto) return TIPO_CURSO;
        return TIPO_ALUMNO;
    }

    /**
     * Retorna la cantidad de ítems en la lista visual.
     *
     * @return cantidad total de elementos (incluye headers, personal, cursos, alumnos)
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Crea el ViewHolder apropiado para cada tipo de ítem.
     *
     * @param parent contenedor padre
     * @param tipo tipo de vista (definido por getItemViewType)
     * @return ViewHolder correspondiente al tipo
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int tipo) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (tipo == TIPO_HEADER) {
            View v = inflater.inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(v);
        } else if (tipo == TIPO_CURSO) {
            View v = inflater.inflate(R.layout.item_curso_expandible, parent, false);
            return new CursoViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_persona, parent, false);
            return new PersonaViewHolder(v);
        }
    }

    /**
     * Vincula datos a cada ViewHolder según la posición en la lista.
     * Configura nombre, estado del checkbox, comportamientos de clic, y sincroniza selección.
     *
     * - En headers: permite expandir/cerrar bloques y seleccionar todos los elementos.
     * - En personal y alumnos: gestiona selección individual y sincroniza con el header.
     * - En cursos: permite expandir para ver alumnos, y selección del curso que afecta a sus alumnos.
     *
     * @param vh ViewHolder a vincular
     * @param position posición actual en la lista visual
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        Object item = items.get(position);

        if (vh instanceof HeaderViewHolder) {
            HeaderViewHolder h = (HeaderViewHolder) vh;
            String texto = (String) item;
            h.tv.setText(texto);

            //  Manejo del título para expandir/cerrar
            h.tv.setOnClickListener(v -> {
                if (texto.contains("Personal")) {
                    expandirPersonal = !expandirPersonal;
                    construirListaVisual();
                    notificarCambio();
                }

                if (texto.contains("Cursos")) {
                    expandirCursos = !expandirCursos;
                    construirListaVisual();
                    notificarCambio();
                }
            });

            //  CHECKBOX — sincronizado
            h.cb.setOnCheckedChangeListener(null);

            if (texto.contains("Personal")) {
                boolean todosMarcados = !personalList.isEmpty()
                        && personalList.stream().allMatch(DestinatarioDto::isSeleccionado);
                h.cb.setChecked(todosMarcados);

                h.cb.setOnCheckedChangeListener((b, c) -> {
                    if (personalList.isEmpty() && listener != null) {
                        listener.onSeleccionCambio(new ArrayList<>(), null, null); // llama al ViewModel
                    }

                    //  marcar todos en memoria
                    for (DestinatarioDto p : personalList) p.setSeleccionado(c);

                    //  reconstruir la lista para que se refleje visualmente
                    construirListaVisual();
                    notificarCambio();
                });
            }

            if (texto.contains("Cursos")) {
                boolean todosCursosMarcados = !cursoList.isEmpty()
                        && cursoList.stream().allMatch(CursoDto::isSeleccionado);
                h.cb.setChecked(todosCursosMarcados);

                h.cb.setOnCheckedChangeListener((b, c) -> {
                    if (cursoList.isEmpty() && listener != null) {
                        listener.onSeleccionCambio(null, new ArrayList<>(), null); // llamada al VM
                    }

                    for (CursoDto curso : cursoList) curso.setSeleccionado(c);
                    for (List<AlumnoDto> alumnos : alumnosPorCurso.values()) {
                        for (AlumnoDto a : alumnos) a.setSeleccionado(c);
                    }
                    notificarCambio();
                    construirListaVisual();
                });
            }

        } else if (vh instanceof PersonaViewHolder) {
            PersonaViewHolder h = (PersonaViewHolder) vh;

            if (item instanceof DestinatarioDto) {
                DestinatarioDto p = (DestinatarioDto) item;
                h.nombre.setText(p.getNombre());
                h.cb.setOnCheckedChangeListener(null);
                h.cb.setChecked(p.isSeleccionado());

                h.cb.setOnCheckedChangeListener((b, c) -> {
                    p.setSeleccionado(c);
                    notificarCambio();

                    // Esto actualiza el checkbox del header “Personal”
                    int headerIndex = items.indexOf("📘 Personal educativo");
                    if (headerIndex != -1) notifyItemChanged(headerIndex);
                });
            } else if (item instanceof AlumnoDto) {
                AlumnoDto a = (AlumnoDto) item;
                h.nombre.setText("👤 " + a.getNombre());
                h.cb.setOnCheckedChangeListener(null);
                h.cb.setChecked(a.isSeleccionado());

                h.cb.setOnCheckedChangeListener((b, c) -> {
                    a.setSeleccionado(c);

                    //verifico el curso al que pertenece
                    for (Map.Entry<Integer, List<AlumnoDto>> entry : alumnosPorCurso.entrySet()) {
                        List<AlumnoDto> alumnos = entry.getValue();
                        if (alumnos != null && alumnos.contains(a)) {
                            boolean todosMarcados = alumnos.stream().allMatch(AlumnoDto::isSeleccionado);
                            for (CursoDto curso : cursoList) {
                                if (curso.getId() == entry.getKey()) {
                                    curso.setSeleccionado(todosMarcados);
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    notificarCambio();
                    construirListaVisual();
                });
            }



        } else if (vh instanceof CursoViewHolder) {
            CursoViewHolder h = (CursoViewHolder) vh;
            CursoDto curso = (CursoDto) item;
            h.nombre.setText("📁 " + curso.getNombre());
            h.cb.setOnCheckedChangeListener(null);
            h.cb.setChecked(curso.isSeleccionado());

            h.cb.setOnCheckedChangeListener((b, checked) -> {
                curso.setSeleccionado(checked);
                List<AlumnoDto> alumnos = alumnosPorCurso.get(curso.getId());
                if (alumnos != null) {
                    for (AlumnoDto a : alumnos) a.setSeleccionado(checked);
                }
                notificarCambio();
                construirListaVisual();
            });

            h.nombre.setOnClickListener(v -> {
                int idCurso = curso.getId();
                boolean yaExpandido = cursosExpandidos.contains(idCurso);

                if (yaExpandido) {
                    cursosExpandidos.remove(idCurso);
                    construirListaVisual();
                } else {
                    cursosExpandidos.add(idCurso);
                    if (expandirListener != null) expandirListener.cargarAlumnosDeCurso(idCurso);
                }
            });
        }
    }

    /**
     * Recolecta los elementos seleccionados de cada grupo (personal, cursos, alumnos)
     * y notifica al listener externo mediante la interfaz `OnSeleccionCambioListener`.
     *
     * Si no hay listener asignado, no hace nada.
     */
    private void notificarCambio() {
        if (listener != null) {
            List<DestinatarioDto> seleccionadosPersonal = new ArrayList<>();
            for (DestinatarioDto p : personalList) if (p.isSeleccionado()) seleccionadosPersonal.add(p);

            List<CursoDto> cursosSel = new ArrayList<>();
            for (CursoDto c : cursoList) if (c.isSeleccionado()) cursosSel.add(c);

            List<AlumnoDto> alumnosSel = new ArrayList<>();
            for (List<AlumnoDto> lista : alumnosPorCurso.values())
                for (AlumnoDto a : lista) if (a.isSeleccionado()) alumnosSel.add(a);

            listener.onSeleccionCambio(seleccionadosPersonal, cursosSel, alumnosSel);
        }
    }

    /**
     * ViewHolder para encabezados de sección ("📘 Personal educativo", "🏫 Cursos").
     * Contiene:
     * - TextView: título del bloque.
     * - CheckBox: permite seleccionar/desmarcar todos los elementos del bloque.
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        CheckBox cb;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvHeader);
            cb = itemView.findViewById(R.id.cbHeader);
        }
    }

    /**
     * ViewHolder para ítems de tipo Persona (DestinatarioDto o AlumnoDto).
     * Contiene:
     * - TextView: nombre del destinatario/alumno.
     * - CheckBox: estado de selección del ítem.
     */
    static class PersonaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        CheckBox cb;
        PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombre);
            cb = itemView.findViewById(R.id.cbPersona);
        }
    }

    /**
     * ViewHolder para cursos (CursoDto).
     * Contiene:
     * - TextView: nombre del curso.
     * - CheckBox: para seleccionar/deseleccionar el curso (y sus alumnos).
     */
    static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        CheckBox cb;
        CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvCursoNombre);
            cb = itemView.findViewById(R.id.chkCurso);
        }
    }

    /**
     * Indica si todos los ítems de personal educativo están seleccionados.
     *
     * @return true si todos los docentes están marcados, false si alguno no lo está.
     */
    public boolean todoElPersonalMarcado() {
        if (personalList.isEmpty()) return false;
        for (DestinatarioDto p : personalList) {
            if (!p.isSeleccionado()) return false;
        }
        return true;
    }

    /**
     * Restablece el estado visual y lógico del adapter:
     * - Contrae todas las secciones (personal, cursos, alumnos).
     * - Desmarca todos los ítems seleccionados.
     * - Limpia cursos expandidos.
     * - Reconstruye la lista visual y la fuerza a redibujarse.
     */
    public void resetearEstadoVisual() {
        expandirPersonal = false;
        expandirCursos = false;
        cursosExpandidos.clear(); // Contrae todos los cursos
        //  Limpieza REAL de estado de selección
        for (DestinatarioDto p : personalList) p.setSeleccionado(false);
        for (CursoDto c : cursoList) c.setSeleccionado(false);
        for (List<AlumnoDto> alumnos : alumnosPorCurso.values()) {
            for (AlumnoDto a : alumnos) a.setSeleccionado(false);
        }
        construirListaVisual();   // Reconstruye la lista vacía
        notifyDataSetChanged();   // Fuerza redibujado
    }

}
