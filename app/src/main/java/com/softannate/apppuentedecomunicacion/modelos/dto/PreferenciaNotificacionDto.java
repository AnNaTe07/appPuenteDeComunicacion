package com.softannate.apppuentedecomunicacion.modelos.dto;

public class PreferenciaNotificacionDto {
    private int canalId;
    private boolean activado;

    public PreferenciaNotificacionDto(int canalId, boolean activado) {
        this.canalId = canalId;
        this.activado = activado;
    }

    public int getCanalId() {
        return canalId;
    }

    public void setCanalId(int canalId) {
        this.canalId = canalId;
    }

    public boolean isActivado() {
        return activado;
    }

    public void setActivado(boolean activado) {
        this.activado = activado;
    }
}
