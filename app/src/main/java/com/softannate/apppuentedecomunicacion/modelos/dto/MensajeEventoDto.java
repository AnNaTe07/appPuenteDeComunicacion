package com.softannate.apppuentedecomunicacion.modelos.dto;
import com.google.gson.annotations.SerializedName;

public class MensajeEventoDto {

    @SerializedName("EstadoId")
    public int EstadoId;

    @SerializedName("MensajeId")
    public int MensajeId;

    @SerializedName("UsuarioDestinoId")
    public int UsuarioDestinoId;


    public MensajeEventoDto(int estadoId, int mensajeId, int usuarioDestinoId) {
        EstadoId = estadoId;
        MensajeId = mensajeId;
        UsuarioDestinoId = usuarioDestinoId;
    }

    public int getEstadoId() {
            return EstadoId;
        }

        public int getMensajeId() {
            return MensajeId;
        }

    public void setEstadoId(int estadoId) {
        this.EstadoId = estadoId;
    }

    public void setMensajeId(int mensajeId) {
        this.MensajeId = mensajeId;
    }

    public int getUsuarioDestinoId() {
        return UsuarioDestinoId;
    }

    public void setUsuarioDestinoId(int UsuarioDestinoId) {
        this.UsuarioDestinoId = UsuarioDestinoId;
    }
}
