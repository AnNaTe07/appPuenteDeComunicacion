package com.softannate.apppuentedecomunicacion.data.api;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeEventoDto;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static WebSocket webSocket;
    private static Context context;
    private static OkHttpClient client = new OkHttpClient();
    private static final String SOCKET_URL = "ws://192.168.1.6:5001/ws";
    private static final Gson gson = new Gson();
    private static EstadoMensajeListener estadoListener;
    private static int usuarioId;

    public static void setEstadoMensajeListener(EstadoMensajeListener listener) {
        estadoListener = listener;
    }

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public interface EstadoMensajeListener {
        void onEstadoActualizado(int mensajeId, int nuevoEstado);
    }

    public static void clearEstadoMensajeListener() {
        estadoListener = null;
    }

    public static void conectar() {

        String tokenJwt = SpManager.getAccessToken(context);
        Log.d("WS", "JWT: " + tokenJwt);
        Request request = new Request.Builder()
                .url(SOCKET_URL)
                .addHeader("Authorization", "Bearer " + tokenJwt)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("WS", "Conexión abierta");
               // webSocket.send("cliente conectado");
                JSONObject json = new JSONObject();
                try {
                    json.put("estadoId", 0); // o cualquier valor que el backend entienda como "conectado"
                    json.put("mensajeId", -1); // o algún ID especial
                    webSocket.send(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                
                Log.d("WS", "Mensaje recibido: " + text);
                // Parsear el JSON recibido
                try {
                    if (text == null || !text.trim().startsWith("{")) return;

                    MensajeEventoDto evento = gson.fromJson(text, MensajeEventoDto.class);
                    if (evento == null) {
                        Log.e("WS", "Error: evento deserializado es null");
                        return;
                    }
                    // Verificar el tipo de evento
                    switch (evento.getEstadoId()) {
                        case 1:
                            // Actualizar estado del mensaje en la UI
                            marcarComoRecibido(evento.getMensajeId(), evento.UsuarioDestinoId);
                            Log.d("WS", "Mensaje RECIBIDO enviado correctamente");
                            break;

                        case 2:
                            Log.d("WS2", "Estado 2 recibido para mensajeId=" + evento.getMensajeId());

                            if (estadoListener != null) {
                                estadoListener.onEstadoActualizado(evento.getMensajeId(), 2);
                            }
                            break;

                        case 3:
                            Log.d("WS", "Estado 3 recibido para mensajeId=" + evento.getMensajeId());

                            // Notificar estado leído
                            if (estadoListener != null) {
                                estadoListener.onEstadoActualizado(evento.getMensajeId(), 3);
                            }
/*
                            // Enviar confirmación al servidor (opcional)
                            MensajeEventoDto recibido = new MensajeEventoDto(2, evento.getMensajeId());
                            String json = gson.toJson(recibido);
                            WebSocketManager.enviarMensaje(json);
 */
                            break;
                        default:
                            Log.w("WS", "Evento desconocido: " + evento.getEstadoId());
                            break;
                    }
                } catch (Exception e) {
                    Log.e("WS", "Error procesando mensaje: " + e.getMessage(), e);

                }
            }


            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("WS", "Error: " + t.getMessage());
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    WebSocketManager.conectar();//reintenta conexion
                }, 3000);

            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d("WS", "Cerrando conexión: " + reason);
            }
        });
    }

    public static void enviarMensaje(String mensaje) {
        if (webSocket != null) {
            webSocket.send(mensaje);
        }
    }

    public static void cerrarConexion() {
        if (webSocket != null) {
            webSocket.close(1000, "App cerrada");
            webSocket = null;
        }
    }

    public static void marcarComoRecibido(int mensajeId, int usuarioId) {
        try {
            JSONObject json = new JSONObject();
            json.put("estadoId", 2); // Estado 2 = RECIBIDO
            json.put("mensajeId", mensajeId);
            json.put("usuarioId", usuarioId); // 👈 Este es el cambio clave

            String mensaje = json.toString();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (webSocket != null && webSocket.send(mensaje)) {
                    Log.d("WebSocket", "Mensaje RECIBIDO enviado correctamente");
                } else {
                    Log.e("WebSocket", "No se pudo enviar el mensaje RECIBIDO");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void confirmarRecepcionMensajes(List<Integer> listaIds) {
        int usuarioId = parseInt(SpManager.getId(context));

        for (int id : listaIds) {
            marcarComoRecibido(id, usuarioId);
        }
    }



}
