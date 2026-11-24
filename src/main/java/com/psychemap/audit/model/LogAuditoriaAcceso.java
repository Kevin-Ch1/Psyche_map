package com.psychemap.audit.model;

import java.time.LocalDateTime;

public class LogAuditoriaAcceso {

    private Integer id;
    private Integer usuarioId;
    private String direccionIp;
    private String moduloConsultado;
    private String accionRealizada;
    private String entidadAfectada;
    private LocalDateTime fechaHoraExacta;

    public LogAuditoriaAcceso() {
        this.fechaHoraExacta = LocalDateTime.now();
    }

    public LogAuditoriaAcceso(Integer usuarioId, String direccionIp, String moduloConsultado,
            String accionRealizada, String entidadAfectada) {
        this.usuarioId = usuarioId;
        this.direccionIp = direccionIp;
        this.moduloConsultado = moduloConsultado;
        this.accionRealizada = accionRealizada;
        this.entidadAfectada = entidadAfectada;
        this.fechaHoraExacta = LocalDateTime.now();
    }

    public LogAuditoriaAcceso(Integer id, Integer usuarioId, String direccionIp, String moduloConsultado,
            String accionRealizada, String entidadAfectada, LocalDateTime fechaHoraExacta) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.direccionIp = direccionIp;
        this.moduloConsultado = moduloConsultado;
        this.accionRealizada = accionRealizada;
        this.entidadAfectada = entidadAfectada;
        this.fechaHoraExacta = fechaHoraExacta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getModuloConsultado() {
        return moduloConsultado;
    }

    public void setModuloConsultado(String moduloConsultado) {
        this.moduloConsultado = moduloConsultado;
    }

    public String getAccionRealizada() {
        return accionRealizada;
    }

    public void setAccionRealizada(String accionRealizada) {
        this.accionRealizada = accionRealizada;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public void setEntidadAfectada(String entidadAfectada) {
        this.entidadAfectada = entidadAfectada;
    }

    public LocalDateTime getFechaHoraExacta() {
        return fechaHoraExacta;
    }

    public void setFechaHoraExacta(LocalDateTime fechaHoraExacta) {
        this.fechaHoraExacta = fechaHoraExacta;
    }
}
