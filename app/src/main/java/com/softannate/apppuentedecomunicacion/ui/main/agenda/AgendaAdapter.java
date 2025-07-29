package com.softannate.apppuentedecomunicacion.ui.main.agenda;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import java.util.HashMap;
import java.util.List;

/**
 * Adaptador personalizado para mostrar una lista de cursos y sus alumnos en un ExpandableListView.
 *
 * <p>Permite agrupar alumnos por curso y manejar la interacción con cada alumno (como iniciar una llamada).
 * Cada grupo representa un curso, y cada hijo representa un alumno que pertenece a ese curso.</p>
 */
public class AgendaAdapter extends BaseExpandableListAdapter {

    /**
     * Interfaz que permite manejar el evento de clic sobre un alumno para realizar acciones como llamar.
     */
    public interface OnAlumnoClickListener {

        /**
         * Método que se dispara cuando se quiere llamar a un alumno.
         *
         * @param alumno Objeto {@link AlumnoDto} seleccionado.
         */
        void onAlumnoLlamar(AlumnoDto alumno);
    }
    private Context context;
    private List<String> cursos;
    private HashMap<String, List<AlumnoDto>> alumnosMap;


    private OnAlumnoClickListener listener;



    /**
     * Constructor del adaptador.
     *
     * @param context Contexto de la aplicación.
     * @param cursos Lista de nombres de cursos.
     * @param alumnosMap Mapa que relaciona cada curso con su lista de alumnos.
     */
    public AgendaAdapter(Context context, List<String> cursos, HashMap<String, List<AlumnoDto>> alumnosMap) {
        this.context = context;
        this.cursos = cursos;
        this.alumnosMap = alumnosMap;
    }

    /**
     * Asigna el listener para manejar clics de llamada sobre alumnos.
     *
     * @param listener Implementación de {@link OnAlumnoClickListener}.
     */
    public void setOnAlumnoClickListener(OnAlumnoClickListener listener) {
        this.listener = listener;
    }
    /**
     * Devuelve la cantidad de grupos (cursos) en el adaptador.
     *
     * @return Número de cursos.
     */
    @Override
    public int getGroupCount() {
        return cursos.size();
    }

    /**
     * Obtiene la posición del grupo dado el nombre del curso.
     *
     * @param nombreCurso Nombre del curso a buscar.
     * @return Índice del curso en la lista, o -1 si no existe.
     */
    public int getGroupPosition(String nombreCurso) {
        return cursos.indexOf(nombreCurso);
    }

    /**
     * Devuelve la cantidad de hijos (alumnos) en un grupo (curso) específico.
     *
     * @param groupPosition Índice del grupo.
     * @return Cantidad de alumnos en el curso.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        String curso = cursos.get(groupPosition);
        List<AlumnoDto> alumnos = alumnosMap.get(curso);
        return (alumnos != null) ? alumnos.size() : 0;
    }

    /**
     * Obtiene el curso en la posición especificada.
     *
     * @param groupPosition Índice del grupo.
     * @return Nombre del curso.
     */
    @Override
    public Object getGroup(int groupPosition) {
        return cursos.get(groupPosition);
    }

    /**
     * Obtiene el alumno en la posición dada dentro de un grupo específico.
     *
     * @param groupPosition Índice del curso.
     * @param childPosition Índice del alumno dentro del curso.
     * @return Objeto {@link AlumnoDto}.
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String curso = cursos.get(groupPosition);
        return alumnosMap.get(curso).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Define si los IDs de grupo e hijo son estables (no lo son en este caso).
     *
     * @return false.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Crea y devuelve la vista para un grupo (curso).
     *
     * @param groupPosition Posición del grupo.
     * @param isExpanded Si el grupo está expandido.
     * @param convertView Vista existente reutilizable.
     * @param parent Grupo padre.
     * @return Vista para el curso.
     */// Curso - grupo
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String curso = (String) getGroup(groupPosition);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_curso_agenda, parent, false);
        TextView texto = convertView.findViewById(R.id.tvCursoAgenda);

        if (curso.equals("Resultados")) {
          texto.setText("");
            return new View(context);
        }

        texto.setText("Curso: " + curso);
        return convertView;
    }

    /**
     * Crea y devuelve la vista para un hijo (alumno).
     *
     * <p>Incluye nombre, teléfono, botón de llamada y comportamiento visual si está seleccionado.</p>
     *
     * @param groupPosition Posición del curso.
     * @param childPosition Posición del alumno.
     * @param isLastChild Si es el último alumno.
     * @param convertView Vista existente reutilizable.
     * @param parent Grupo padre.
     * @return Vista para el alumno.
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        AlumnoDto alumno = (AlumnoDto) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_agenda, parent, false);
        }

        TextView nombre = convertView.findViewById(R.id.tvNombreAgenda);

        List<String> telefonos = alumno.getTelefonos();

        TextView telefono = convertView.findViewById(R.id.tvTelefono);
        nombre.setText(alumno.getNombre());

        String telefonoTexto = (telefonos != null && !telefonos.isEmpty())
                ? TextUtils.join("\n", telefonos)
                : "Sin teléfono";
        telefono.setText("Tel: " + telefonoTexto);



        nombre.setOnClickListener(v -> {
            for (List<AlumnoDto> grupo : alumnosMap.values()) {
                for (AlumnoDto a : grupo) {
                    a.setSeleccionado(false);
                }
            }
            alumno.setSeleccionado(true);
            notifyDataSetChanged();
        });


        ImageButton btnLlamar = convertView.findViewById(R.id.btnLlamar);

        btnLlamar.setVisibility(View.GONE);
        if (alumno.isSeleccionado()) {
            btnLlamar.setVisibility(View.VISIBLE);
        }

        btnLlamar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlumnoLlamar((AlumnoDto) getChild(groupPosition, childPosition));
            }
        });

        return convertView;
    }

    /**
     * Indica si el alumno puede ser seleccionado.
     *
     * @param groupPosition Índice del curso.
     * @param childPosition Índice del alumno.
     * @return true.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /**
     * Actualiza la lista de cursos en el adaptador.
     *
     * @param nuevosCursos Nueva lista de nombres de cursos.
     */
    public void setCursos(List<String> nuevosCursos) {
        cursos.clear();
        cursos.addAll(nuevosCursos);
        notifyDataSetChanged();
    }

    /**
     * Reemplaza la lista de alumnos asociada a un curso específico.
     *
     * @param curso Nombre del curso.
     * @param alumnos Lista de alumnos nueva.
     */
    public void setAlumnos(String curso, List<AlumnoDto> alumnos) {
        alumnosMap.put(curso, alumnos);
        notifyDataSetChanged();
    }


}
