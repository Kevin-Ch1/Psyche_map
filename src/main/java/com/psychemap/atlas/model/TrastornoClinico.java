package com.psychemap.atlas.model;

import java.time.LocalDateTime;

public class TrastornoClinico {

    private Integer id;
    private String codigoClasificacion;
    private String nombreTrastorno;
    private String categoriaNosologica;
    private String edadInicioPromedio;
    private String prevalenciaEstimada;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private String estadoRegistro;

    public TrastornoClinico() {
        this.creadoEn = LocalDateTime.now();
        this.estadoRegistro = "Activo";
    }

    public TrastornoClinico(String codigoClasificacion, String nombreTrastorno, String categoriaNosologica) {
        this();
        this.codigoClasificacion = codigoClasificacion;
        this.nombreTrastorno = nombreTrastorno;
        this.categoriaNosologica = categoriaNosologica;
    }

    public TrastornoClinico(Integer id, String codigoClasificacion, String nombreTrastorno, String categoriaNosologica,
            String edadInicioPromedio, String prevalenciaEstimada, LocalDateTime creadoEn,
            LocalDateTime actualizadoEn, String estadoRegistro) {
        this.id = id;
        this.codigoClasificacion = codigoClasificacion;
        this.nombreTrastorno = nombreTrastorno;
        this.categoriaNosologica = categoriaNosologica;
        this.edadInicioPromedio = edadInicioPromedio;
        this.prevalenciaEstimada = prevalenciaEstimada;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.estadoRegistro = estadoRegistro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoClasificacion() {
        return codigoClasificacion;
    }

    public void setCodigoClasificacion(String codigoClasificacion) {
        this.codigoClasificacion = codigoClasificacion;
    }

    public String getNombreTrastorno() {
        return nombreTrastorno;
    }

    public void setNombreTrastorno(String nombreTrastorno) {
        this.nombreTrastorno = nombreTrastorno;
    }

    public String getCategoriaNosologica() {
        return categoriaNosologica;
    }

    public void setCategoriaNosologica(String categoriaNosologica) {
        this.categoriaNosologica = categoriaNosologica;
    }

    public String getEdadInicioPromedio() {
        return edadInicioPromedio;
    }

    public void setEdadInicioPromedio(String edadInicioPromedio) {
        this.edadInicioPromedio = edadInicioPromedio;
    }

    public String getPrevalenciaEstimada() {
        return prevalenciaEstimada;
    }

    public void setPrevalenciaEstimada(String prevalenciaEstimada) {
        this.prevalenciaEstimada = prevalenciaEstimada;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public String getEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(String estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }
}
