package com.psychemap.security.model;

import java.time.LocalDateTime;

public class RolPermiso {

    private Integer id;
    private Integer usuarioId;
    private String nombreRol;
    private boolean permisoLectura;
    private boolean permisoEscritura;
    private boolean permisoExportacion;
    private LocalDateTime creadoEn;

    public RolPermiso() {
    }

    public RolPermiso(Integer usuarioId, String nombreRol, boolean permisoLectura, boolean permisoEscritura,
            boolean permisoExportacion) {
        this.usuarioId = usuarioId;
        this.nombreRol = nombreRol;
        this.permisoLectura = permisoLectura;
        this.permisoEscritura = permisoEscritura;
        this.permisoExportacion = permisoExportacion;
        this.creadoEn = LocalDateTime.now();
    }

    public RolPermiso(Integer id, Integer usuarioId, String nombreRol, boolean permisoLectura, boolean permisoEscritura,
            boolean permisoExportacion, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreRol = nombreRol;
        this.permisoLectura = permisoLectura;
        this.permisoEscritura = permisoEscritura;
        this.permisoExportacion = permisoExportacion;
        this.creadoEn = creadoEn;
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

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public RolNombre getRolEnum() {
        return RolNombre.fromString(this.nombreRol);
    }

    public boolean isPermisoLectura() {
        return permisoLectura;
    }

    public void setPermisoLectura(boolean permisoLectura) {
        this.permisoLectura = permisoLectura;
    }

    public boolean isPermisoEscritura() {
        return permisoEscritura;
    }

    public void setPermisoEscritura(boolean permisoEscritura) {
        this.permisoEscritura = permisoEscritura;
    }

    public boolean isPermisoExportacion() {
        return permisoExportacion;
    }

    public void setPermisoExportacion(boolean permisoExportacion) {
        this.permisoExportacion = permisoExportacion;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
