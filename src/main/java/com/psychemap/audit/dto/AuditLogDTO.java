package com.psychemap.audit.dto;

import java.time.LocalDateTime;

public class AuditLogDTO {

    private int id;
    private String nombreUsuario;
    private String direccionIp;
    private String moduloConsultado;
    private String accionRealizada;
    private String entidadAfectada;
    private LocalDateTime fechaHoraExacta;

    public AuditLogDTO(int id, String nombreUsuario, String direccionIp, String moduloConsultado,
                       String accionRealizada, String entidadAfectada, LocalDateTime fechaHoraExacta) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.direccionIp = direccionIp;
        this.moduloConsultado = moduloConsultado;
        this.accionRealizada = accionRealizada;
        this.entidadAfectada = entidadAfectada;
        this.fechaHoraExacta = fechaHoraExacta;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public String getModuloConsultado() {
        return moduloConsultado;
    }

    public String getAccionRealizada() {
        return accionRealizada;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public LocalDateTime getFechaHoraExacta() {
        return fechaHoraExacta;
    }
}
