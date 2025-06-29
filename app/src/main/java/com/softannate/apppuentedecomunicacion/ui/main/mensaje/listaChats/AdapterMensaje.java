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

public class AdapterMensaje extends RecyclerView.Adapter<AdapterMensaje.ViewHolderMensaje> {

    private List<MensajeDTO> mensajes;
    private final NavController navController;
    private String filtro = "";

    public AdapterMensaje( String filtro,List<MensajeDTO> mensajes, NavController navController) {
        this.filtro = filtro;
        this.mensajes = mensajes;
        this.navController = navController;
    }

    public void setMensajes(String filtro,List<MensajeDTO> mensajes) {
        this.filtro = filtro;
        this.mensajes = mensajes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);

        Log.d("Adapter", "View inflated: " + view);
        return new ViewHolderMensaje(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMensaje holder, int position) {
        MensajeDTO mensaje = mensajes.get(position);
        Context context = holder.itemView.getContext();
        int color = Color.parseColor("#FFECB3");

        holder.nombreUsuario.setText(ResaltadorHelper.resaltarFondoCoincidencias(obtenerNombreParaMostrar(mensaje, context), filtro, color));

        holder.mensajeUsuario.setText(ResaltadorHelper.resaltarFondoCoincidencias(mensaje.getContenido(),filtro,color));
        holder.nombreAlumno.setText(ResaltadorHelper.resaltarFondoCoincidencias(mensaje.getAlumnoNombre(),filtro,color));
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

    public class ViewHolderMensaje extends RecyclerView.ViewHolder {

        TextView nombreUsuario, mensajeUsuario, nombreAlumno, fechaMensaje,badgeChat, categoria;
        ImageView imgUsuario;


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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View view) {
                        MensajeDTO mensaje = mensajes.get(getAdapterPosition());
                        Context context = itemView.getContext();
                        int userLogueadoId = Integer.parseInt(SpManager.getId(context));
                        String rol = SpManager.getRol(context);

                        int alumnoId = mensaje.getAlumnoId();
                        String alumnoNombre = (alumnoId == 0) ? "" : "(" + mensaje.getAlumnoNombre() + ")";

                        String avatar = obtenerAvatarParaMostrar(mensaje, context);
                        String userNombre = obtenerNombreParaMostrar(mensaje, context);

                        Bundle bundle = new Bundle();
                        bundle.putString("avatar", avatar);
                        bundle.putString("userNombre", userNombre);
                        bundle.putString("alumnoNombre", alumnoNombre);
                        bundle.putInt("alumnoId", alumnoId);

                        // ⚠️ SIEMPRE PASAMOS LOS DOS PARTICIPANTES
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
    private String obtenerNombreParaMostrar(MensajeDTO mensaje, Context context) {
        int userId = Integer.parseInt(SpManager.getId(context));
        String rol = SpManager.getRol(context);

        if ("2".equals(rol) && userId != mensaje.getEmisorId() && userId != mensaje.getReceptorId()) {
            Log.d("adapter" ,"emisor: " + mensaje.getEmisorId() + " receptor: " + mensaje.getReceptorId() + " userId: " + userId);
            return mensaje.getEmisorNombre() + " ➜ " + mensaje.getReceptorNombre();
        }

        return (userId == mensaje.getReceptorId())
                ? mensaje.getEmisorNombre()
                : mensaje.getReceptorNombre();
    }
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
