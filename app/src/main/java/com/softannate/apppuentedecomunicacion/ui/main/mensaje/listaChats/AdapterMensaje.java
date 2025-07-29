package com.softannate.apppuentedecomunicacion.ui.main.mensaje.listaChats;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.utils.DateUtils;
import com.softannate.apppuentedecomunicacion.utils.ResaltadorHelper;
import java.util.List;

/**
 * AdapterMensaje es el adaptador del RecyclerView encargado de mostrar una lista
 * de mensajes en la interfaz del chat. Utiliza el patrón ViewHolder para optimizar
 * el rendimiento y maneja el filtrado dinámico de coincidencias en los textos.
 */
public class AdapterMensaje extends RecyclerView.Adapter<AdapterMensaje.ViewHolderMensaje> {

    // Lista de mensajes a mostrar
    private List<MensajeDTO> mensajes;

    // Controlador de navegación para redireccionar al fragmento de conversación
    private final NavController navController;

    // Texto para aplicar filtros de coincidencia visual en los ítems
    private String filtro = "";

    /**
     * Constructor del adaptador.
     *
     * @param filtro Texto usado para resaltar coincidencias en los mensajes.
     * @param mensajes Lista de mensajes a mostrar en el RecyclerView.
     * @param navController Controlador de navegación entre fragments.
     */
    public AdapterMensaje( String filtro,List<MensajeDTO> mensajes, NavController navController) {
        this.filtro = filtro;
        this.mensajes = mensajes;
        this.navController = navController;
    }

    /**
     * Actualiza la lista de mensajes y el filtro, notificando los cambios al RecyclerView.
     *
     * @param filtro Texto de búsqueda o coincidencia para resaltar.
     * @param mensajes Nueva lista de mensajes a mostrar.
     */
    public void setMensajes(String filtro,List<MensajeDTO> mensajes) {
        this.filtro = filtro;
        this.mensajes = mensajes;
        notifyDataSetChanged();
    }

    /**
     * Infla la vista del ítem y crea el ViewHolder correspondiente.
     *
     * @param parent Vista padre del RecyclerView.
     * @param viewType Tipo de vista (no usado aquí, ya que solo hay un tipo).
     * @return ViewHolderMensaje que contiene las vistas del ítem.
     */
    @NonNull
    @Override
    public ViewHolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);

        Log.d("Adapter", "View inflated: " + view);
        return new ViewHolderMensaje(view);
    }

    /**
     * Vincula los datos de un mensaje con las vistas correspondientes en el ViewHolder.
     * Aplica resaltado según coincidencias con el filtro activo y configura la visualización
     * del avatar, nombre de usuario, contenido, alumno, fecha, categoría y mensajes no leídos.
     *
     * @param holder ViewHolderMensaje que contiene las vistas del ítem.
     * @param position Posición del mensaje dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderMensaje holder, int position) {
        MensajeDTO mensaje = mensajes.get(position);
        Context context = holder.itemView.getContext();
        int color = Color.parseColor("#FFECB3");

        String nombre= obtenerNombreParaMostrar(mensaje, context);
        if (nombre.contains("➜")) {
            holder.nombreUsuario.setMaxLines(2);
        }
        holder.nombreUsuario.setText(ResaltadorHelper.resaltarFondoCoincidencias(nombre, filtro, color));

        holder.mensajeUsuario.setText(ResaltadorHelper.resaltarFondoCoincidencias(mensaje.getContenido(),filtro,color));

        if (mensaje.getAlumnoNombre() != null && mensaje.getAlumnoNombre().length() > 4) {//mensaje.getAlumnoNombre().trim()length() > 1
           holder.nombreAlumno.setText(ResaltadorHelper.resaltarFondoCoincidencias(mensaje.getAlumnoNombre(), filtro, color));
       }else{
           holder.nombreAlumno.setText(" ");
       }

       holder.fechaMensaje.setText(ResaltadorHelper.resaltarFondoCoincidencias(DateUtils.formatearFecha(mensaje.getFecha_Hora()),filtro,color));
        holder.categoria.setText(ResaltadorHelper.resaltarFondoCoincidencias(mensaje.getCategoria(),filtro,color));
        int cantidad = mensaje.getMensajesNoLeido();
        if (cantidad > 0) {
            holder.badgeChat.setVisibility(View.VISIBLE);
            holder.badgeChat.setText(String.valueOf(cantidad));
        } else {
            holder.badgeChat.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(obtenerAvatarParaMostrar(mensaje, context)) //cargamos la imagen del usuario())
                .placeholder(R.drawable.cargando)//imagen temporal mientras se carga
                .diskCacheStrategy(DiskCacheStrategy.NONE)//desactivo la cache
                .skipMemoryCache(true)
                .error(R.drawable.perfil_user)
                .into(holder.imgUsuario);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    /**
     * ViewHolderMensaje es responsable de vincular los datos de un mensaje con las vistas
     * del ítem dentro de un RecyclerView. Maneja también el evento de clic, preparando
     * la navegación hacia el fragmento de conversación según el rol del usuario.
     */
    public class ViewHolderMensaje extends RecyclerView.ViewHolder {

        TextView nombreUsuario, mensajeUsuario, nombreAlumno, fechaMensaje,badgeChat, categoria;
        ImageView imgUsuario;

        /**
         * Inicializa las vistas y define el comportamiento al hacer clic.
         * Crea un Bundle con datos contextuales del mensaje y usuario para navegar.
         *
         * @param itemView Vista del ítem en el RecyclerView.
         */
        public ViewHolderMensaje(@NonNull View itemView) {
            super(itemView);

            //inicializo las vistas que siempre están presentes
            fechaMensaje = itemView.findViewById(R.id.fechaUsuario);
            nombreAlumno = itemView.findViewById(R.id.nombreAlumno);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            mensajeUsuario= itemView.findViewById(R.id.mensajeUsuario);
            badgeChat=itemView.findViewById(R.id.badgeChat);
            categoria=itemView.findViewById(R.id.categoria);

            // Define el listener para navegar al fragmento de conversación
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View view) {
                        MensajeDTO mensaje = mensajes.get(getAdapterPosition());
                        Context context = itemView.getContext();
                        int userLogueadoId = Integer.parseInt(SpManager.getId(context));
                        String rol = SpManager.getRol(context);

                        int alumnoId = mensaje.getAlumnoId();
                        String alumnoNombre = (alumnoId == 0) ? "" : mensaje.getAlumnoNombre();

                        String avatar = obtenerAvatarParaMostrar(mensaje, context);
                        String userNombre = obtenerNombreParaMostrar(mensaje, context);

                        Bundle bundle = new Bundle();
                        bundle.putString("avatar", avatar);
                        bundle.putString("userNombre", userNombre);
                        bundle.putString("alumnoNombre", alumnoNombre);
                        bundle.putInt("alumnoId", alumnoId);
                        bundle.putInt("rol", Integer.parseInt(rol));

                        //  SIEMPRE PASO LOS DOS PARTICIPANTES
                        bundle.putInt("emisorId", mensaje.getEmisorId());
                        bundle.putInt("receptorId", mensaje.getReceptorId());

                        // Solo agregamos userId si el usuario participa (para el fragment)
                        if (!("2".equals(rol) && userLogueadoId != mensaje.getEmisorId() && userLogueadoId != mensaje.getReceptorId())) {
                            int otroUserId = (mensaje.getReceptorId() == userLogueadoId)
                                    ? mensaje.getEmisorId()
                                    : mensaje.getReceptorId();
                            bundle.putInt("userId", otroUserId);
                        }

                        Log.d("Adapter", "EmisorId: " + mensaje.getEmisorId());
                        Log.d("Adapter", "ReceptorId: " + mensaje.getReceptorId());
                        Log.d("Adapter", "AlumnoId: " + alumnoId);
                        Log.d("Adapter", "Avatar: " + avatar);
                        Log.d("Adapter", "Nombre mostrado: " + userNombre);

                        navController.navigate(R.id.nav_conversacion, bundle);
                    }
                });
        }
    }

    /**
     * Determina qué nombre mostrar en un chat según el rol y participación del usuario.
     *
     * <p>Si el usuario tiene rol de director (rol "2") y no participa en el mensaje
     * como emisor ni receptor, se muestra una representación con íconos que identifica
     * al emisor y receptor del mensaje.</p>
     *
     * <p>Si el usuario es participante, se muestra el nombre de su interlocutor:
     * si es receptor, se muestra el nombre del emisor; si es emisor, el del receptor.</p>
     *
     * @param mensaje Objeto MensajeDTO que contiene datos del mensaje, emisor y receptor.
     * @param context Contexto necesario para acceder al ID y rol del usuario.
     * @return Nombre que debe mostrarse en la interfaz del chat.
     */
    private String obtenerNombreParaMostrar(MensajeDTO mensaje, Context context) {
        int userId = Integer.parseInt(SpManager.getId(context));
        String rol = SpManager.getRol(context);

        if ("2".equals(rol) && userId != mensaje.getEmisorId() && userId != mensaje.getReceptorId()) {
            Log.d("adapter" ,"emisor: " + mensaje.getEmisorId() + " receptor: " + mensaje.getReceptorId() + " userId: " + userId);
            return "\uD83D\uDC64" + mensaje.getEmisorNombre() + "➜" +"\n\uD83D\uDC64 "+ mensaje.getReceptorNombre();
        }

        return (userId == mensaje.getReceptorId())
                ? mensaje.getEmisorNombre()
                : mensaje.getReceptorNombre();
    }

    /**
     * Determina qué avatar mostrar en función del rol del usuario y su participación en el mensaje.
     *
     * <p>Si el usuario tiene rol de director y no está involucrado en el mensaje (ni como emisor ni como receptor),
     * se retorna el avatar del emisor, suponiendo que es quien habló más recientemente.</p>
     *
     * <p>Si el usuario está involucrado en el mensaje, se retorna el avatar del interlocutor:
     * si el usuario es el receptor, se muestra el avatar del emisor; en caso contrario, el del receptor.</p>
     *
     * @param mensaje El mensaje con información de emisor, receptor y avatares.
     * @param context Contexto necesario para obtener el ID y rol del usuario.
     * @return El avatar correspondiente que debe ser mostrado.
     */
    private String obtenerAvatarParaMostrar(MensajeDTO mensaje, Context context) {
        int userId = Integer.parseInt(SpManager.getId(context));
        String rol = SpManager.getRol(context);

        boolean esDirector = "2".equals(rol);
        boolean usuarioNoParticipa = userId != mensaje.getEmisorId() && userId != mensaje.getReceptorId();

        // Si el usuario no está involucrado, retorno el avatar del emisor (último que habló)
        if (esDirector && usuarioNoParticipa) {
            return mensaje.getAvatar(); // asumo que este es del emisor
        }

        // Si el usuario es receptor, mostrar el avatar del emisor
        return userId == mensaje.getReceptorId()
                ? mensaje.getAvatar()
                : mensaje.getAvatarR();
    }
}
