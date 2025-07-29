package com.softannate.apppuentedecomunicacion.ui.main.mensaje.conversacion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.modelos.Archivo_adjunto;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import com.softannate.apppuentedecomunicacion.utils.DateUtils;
import com.softannate.apppuentedecomunicacion.utils.PdfUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una conversación dentro de un RecyclerView.
 *
 * <p>Este adaptador gestiona una lista de mensajes {@link MensajeDTO},
 * identificando visualmente quién los envía según el {@code idVisual}.
 * Permite definir un listener para manejar clics en archivos adjuntos.</p>
 */
public class ConversacionAdapter extends RecyclerView.Adapter<ConversacionAdapter.MensajeViewHolder> {
    private List<MensajeDTO> mensajes;
    private int idVisual;

    /**
     * Interfaz para manejar eventos de clic sobre archivos adjuntos en un mensaje.
     */
    public interface OnArchivoClickListener {
        /**
         * Se ejecuta cuando se hace clic sobre un archivo adjunto.
         *
         * @param archivo El archivo adjunto seleccionado.
         */
        void onArchivoClick(Archivo_adjunto archivo);
    }

    private OnArchivoClickListener eventoArchivoClickListener;

    /**
     * Asigna el listener que responde a clics en archivos adjuntos.
     *
     * @param listener Implementación de {@link OnArchivoClickListener}.
     */
    public void setOnArchivoClickListener(OnArchivoClickListener listener) {
        this.eventoArchivoClickListener = listener;
    }


    /**
     * Constructor del adaptador de conversación.
     *
     * @param mensajes Lista de mensajes a mostrar en la conversación.
     * @param idVisual ID que representa al usuario actual, utilizado para distinguir mensajes enviados y recibidos.
     */
    public ConversacionAdapter(List<MensajeDTO> mensajes, int idVisual) {
        this.mensajes = mensajes;
        this.idVisual = idVisual;
    }

    /**
     * Actualiza la lista de mensajes en el adaptador.
     *
     * <p>Si la lista proporcionada es nula, se inicializa como vacía.
     * Luego se notifica al adaptador que los datos han cambiado para refrescar la vista.</p>
     *
     * @param mensajes Lista de mensajes actualizada.
     */
    public void setListaMensajes(List<MensajeDTO> mensajes) {
        if (mensajes == null) {
            this.mensajes = new ArrayList<>();
        } else {
            this.mensajes = mensajes;
        }
        notifyDataSetChanged();
    }

    /**
     * Crea un nuevo ViewHolder para representar un ítem de mensaje.
     *
     * <p>Infla el layout {@code item_mensaje} y lo asocia a un nuevo {@link MensajeViewHolder}.</p>
     *
     * @param parent Vista padre del RecyclerView.
     * @param viewType Tipo de vista (no utilizado en esta implementación).
     * @return ViewHolder con el layout del mensaje.
     */
    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    /**
     * Asocia los datos del mensaje a las vistas correspondientes dentro del ViewHolder.
     *
     * <p>Este método personaliza la visualización de cada mensaje dependiendo de su emisor,
     * el estado de lectura, la fecha/hora, y los archivos adjuntos.</p>
     *
     * <ul>
     *   <li>Determina si el mensaje fue enviado por el usuario logueado para ajustar el diseño (alineación, fondo).</li>
     *   <li>Muestra fecha y hora del mensaje, evitando repetición de fechas si ya están visibles en el mensaje anterior.</li>
     *   <li>Configura los íconos de estado del mensaje (enviado, no leído, leído) usando filtros de color.</li>
     *   <li>Renderiza el contenido textual y categoría del mensaje.</li>
     *   <li>Muestra los archivos adjuntos con vistas dinámicas, íconos representativos según MIME, y vista previa si aplica.</li>
     *   <li>Usa Glide para mostrar imágenes, y utilidades propias para renderizar PDFs.</li>
     * </ul>
     *
     * @param holder ViewHolder que contiene las vistas del ítem.
     * @param position Índice del mensaje dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        MensajeDTO mensaje = mensajes.get(position);
        List<Archivo_adjunto> archivos = mensaje.getArchivos();
        Context context = holder.itemView.getContext();
        String fechaCompleta = mensaje.getFecha_Hora();

        boolean usuarioLogueado = mensaje.getEmisorId() == idVisual;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layout.getLayoutParams();
        params.gravity = usuarioLogueado ? Gravity.LEFT : Gravity.RIGHT;

        int fondo = usuarioLogueado ? R.drawable.no_leido : R.drawable.leido;
        holder.layout.setBackgroundResource(fondo);

        DisplayMetrics displayMetrics = holder.itemView.getContext().getResources().getDisplayMetrics();
        int maxWidth = (int) (displayMetrics.widthPixels * 0.75);

        params.width = maxWidth;
        holder.layout.setLayoutParams(params);

        Log.d("DEBUG_VIEW", "Mensaje EmisorId: " + mensaje.getEmisorId() + ", ID Visual: " + idVisual);


        if(fechaCompleta!=null) {
            String[] partes = fechaCompleta.split("T");

            String fecha = DateUtils.formatearFecha(fechaCompleta);//"dd/MM/yyyy"
            String hora = partes[1].substring(0, 5); // "HH:mm"

            holder.layout.setVisibility(View.VISIBLE);
            holder.hora.setText(hora);

            if (position == 0 || !fecha.equals(getFechaAnterior(position))) {
                holder.fecha.setVisibility(View.VISIBLE);
                holder.fecha.setText(fecha);
            } else {
                holder.fecha.setVisibility(View.GONE);
            }

            int estado = mensaje.getEstadoId();
            Log.d("ConversacionAdapter", "cantidad: " + estado);
            String rol = SpManager.getRol(holder.itemView.getContext());

            int idLogueado = Integer.parseInt(SpManager.getId(holder.itemView.getContext()));

            if (mensaje.getEmisorId() != idVisual || ("2".equals(rol) && idLogueado != mensaje.getEmisorId() && idLogueado != mensaje.getReceptorId())) {
               if (estado == 2) {//no leido
                    holder.check1.setVisibility(View.VISIBLE);
                    holder.check2.setVisibility(View.VISIBLE);
                    holder.check1.setColorFilter(Color.GRAY);
                    holder.check2.setColorFilter(Color.GRAY);
                } else if (estado == 3) {//leido
                    //int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorBase);

                    holder.check1.setVisibility(View.VISIBLE);
                    holder.check2.setVisibility(View.VISIBLE);
                    holder.check1.setColorFilter(Color.rgb(79, 195, 247));
                    holder.check2.setColorFilter(Color.rgb(79, 195, 247));
                } else if (estado == 1) {//enviado
                    holder.check1.setVisibility(View.VISIBLE);
                    holder.check1.setColorFilter(Color.GRAY);
                }
            } else {
                holder.check1.setVisibility(View.GONE);
                holder.check2.setVisibility(View.GONE);
            }
            holder.contenido.setText(mensaje.getContenido());
            holder.categoria.setText(mensaje.getCategoria());
        }


        if (archivos != null && !archivos.isEmpty()) {
            holder.layoutArchivos.setVisibility(View.VISIBLE);
            holder.layoutArchivos.removeAllViews();

            for (Archivo_adjunto archivo : archivos) {
                View vistaArchivo = LayoutInflater.from(context)
                        .inflate(R.layout.vista_archivo_compacta, holder.layoutArchivos, false);

                TextView nombre = vistaArchivo.findViewById(R.id.tvNombreArchivo);
                ImageView icono = vistaArchivo.findViewById(R.id.imgTipoArchivo);
                ImageView preview = vistaArchivo.findViewById(R.id.imgPreviewArchivo);

                nombre.setText(archivo.getNombre());
                Log.d("MIME-TIPO", "📎 MIME recibido: " + archivo.getTipoMime());

                String tipo = detectarTipoVisual(archivo.getTipoMime());
                File archivoLocal = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), archivo.getNombre());

                switch (tipo) {
                    case "imagen":
                        preview.setVisibility(View.VISIBLE);

                        Log.d("ARCHIVO", "Ruta: " + archivoLocal.getAbsolutePath() + " / existe?: " + archivoLocal.exists());

                        Glide.with(context)
                                .load(archivo.getUrl()) //  URL directa de Firebase
                                .centerCrop()
                                .placeholder(R.drawable.ic_image)
                                .error(R.drawable.close)
                                .into(preview);

                        break;

                    case "pdf":
                        preview.setVisibility(View.VISIBLE);
                        Bitmap miniatura = PdfUtils.renderPaginaPDF(context, archivoLocal.getAbsolutePath());

                        if (miniatura != null) {
                            //  Escalar la miniatura al tamaño del ImageView
                            preview.post(() -> {
                                int ancho = preview.getWidth();
                                int alto = preview.getHeight();

                                if (ancho > 0 && alto > 0) {
                                    Bitmap miniaturaEscalada = Bitmap.createScaledBitmap(miniatura, ancho, alto, true);
                                    preview.setImageBitmap(miniaturaEscalada);
                                } else {
                                    preview.setImageBitmap(miniatura); // fallback si no hay tamaño aún
                                }
                            });
                        } else {
                            preview.setImageResource(R.drawable.ic_pdf);
                        }

                        icono.setImageResource(R.drawable.ic_pdf);
                        break;


                    case "word":
                        preview.setVisibility(View.VISIBLE);
                        preview.setImageResource(R.drawable.docx);
                        icono.setImageResource(R.drawable.docx);
                        break;

                    case "excel":
                        preview.setVisibility(View.VISIBLE);
                        preview.setImageResource(R.drawable.excel);
                        icono.setImageResource(R.drawable.excel);
                        break;

                    case "video":
                        preview.setVisibility(View.VISIBLE);
                        preview.setImageResource(R.drawable.ivideo);
                        icono.setImageResource(R.drawable.ic_video);
                        break;

                    case "audio":
                        preview.setVisibility(View.VISIBLE);
                        preview.setImageResource(R.drawable.sound);
                        icono.setImageResource(R.drawable.ic_audio);
                        break;

                    default:
                        preview.setVisibility(View.VISIBLE);
                        preview.setImageResource(R.drawable.clip);
                        icono.setImageResource(R.drawable.clip);
                        break;
                }

                vistaArchivo.setOnClickListener(v -> {
                    if (eventoArchivoClickListener != null) {
                        eventoArchivoClickListener.onArchivoClick(archivo);
                    }
                });

                holder.layoutArchivos.addView(vistaArchivo);
            }
        } else {
            holder.layoutArchivos.setVisibility(View.GONE);
        }
    }

    /**
     * Devuelve la cantidad total de ítems (mensajes) que maneja el adaptador.
     *
     * <p>Si la lista de mensajes es nula, se retorna 0; en caso contrario,
     * se retorna el tamaño de la lista, lo cual permite al RecyclerView
     * conocer cuántos elementos debe renderizar.</p>
     *
     * @return Número total de mensajes que se muestran en el RecyclerView.
     */
    @Override
    public int getItemCount() {
        return  (mensajes != null) ? mensajes.size() : 0;
    }

    /**
     * Obtiene la fecha del mensaje anterior a la posición actual en formato legible.
     *
     * <p>Extrae la cadena de fecha y hora del mensaje anterior y la procesa con
     * {@link DateUtils#formatearFecha} si contiene un separador estándar ("T").</p>
     *
     * @param position Índice actual del mensaje.
     * @return Fecha formateada del mensaje anterior, o una cadena vacía si no está disponible.
     */
    private String getFechaAnterior(int position) {
        if (position > 0) {
            String fechaAnteriorCompleta = mensajes.get(position - 1).getFecha_Hora();
            if (fechaAnteriorCompleta != null && fechaAnteriorCompleta.contains("T")) {
                return DateUtils.formatearFecha(fechaAnteriorCompleta);
            }
        }
        return "";
    }

    /**
     * ViewHolder que encapsula la vista de cada ítem de mensaje en el RecyclerView.
     *
     * <p>Contiene referencias a vistas de texto, imagen, indicadores de estado de envío,
     * fecha, hora y layout de archivos adjuntos, que serán utilizados para enlazar los datos.</p>
     */
    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout, layoutArchivos;
        TextView contenido, hora, fecha, categoria;
        ImageView foto, check1, check2, preview;

        public MensajeViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layoutMensaje);
            contenido=view.findViewById(R.id.textoMensaje);
            hora=view.findViewById(R.id.horaMensaje);
            fecha=view.findViewById(R.id.tvFecha);
            categoria=view.findViewById(R.id.categoria);
            foto=view.findViewById(R.id.fotoUsuario);
            check1=view.findViewById(R.id.check1);
            check2=view.findViewById(R.id.check2);
            layoutArchivos = view.findViewById(R.id.layoutArchivoAdjunto);
            preview = view.findViewById(R.id.imgPreviewArchivo);

        }
    }

    /**
     * Determina el tipo visual a mostrar según el tipo MIME de un archivo.
     *
     * <p>Clasifica el MIME en categorías como imagen, PDF, audio, video, Word, Excel u "otro"
     * para facilitar la visualización adecuada de los adjuntos.</p>
     *
     * @param mime Cadena MIME que describe el tipo de contenido del archivo.
     * @return Tipo de visualización: "imagen", "pdf", "audio", "video", "word", "excel" u "otro".
     */
    private String detectarTipoVisual(String mime) {
        Log.d("MIME-TIPO", "📎 MIME recibido: " + mime);

        if (mime == null || mime.isEmpty()) return "otro";

        mime = mime.toLowerCase();

        if (mime.startsWith("image/")) return "imagen";
        if (mime.equals("application/pdf")) return "pdf";
        if (mime.startsWith("audio/")) return "audio";
        if (mime.startsWith("video/")) return "video";

        if (mime.contains("msword") || mime.contains("wordprocessingml")) return "word";
        if (mime.contains("excel") || mime.contains("spreadsheetml")) return "excel";

        return "otro";
    }

}
