package com.psychemap.atlas.model;

import java.time.LocalDateTime;

public class CriterioDiagnostico {
    private Integer id;
    private Integer trastornoId;
    private String identificadorCriterio;
    private String descripcionClinica;
    private Integer duracionMinimaMeses;
    private Integer umbralSintomasRequerido;
    private String tipoCriterio;
    private String fuenteManual;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public CriterioDiagnostico() {
    }

    public CriterioDiagnostico(Integer id, Integer trastornoId, String identificadorCriterio,
                               String descripcionClinica, Integer duracionMinimaMeses,
                               Integer umbralSintomasRequerido, String tipoCriterio, String fuenteManual) {
        this.id = id;
        this.trastornoId = trastornoId;
        this.identificadorCriterio = identificadorCriterio;
        this.descripcionClinica = descripcionClinica;
        this.duracionMinimaMeses = duracionMinimaMeses;
        this.umbralSintomasRequerido = umbralSintomasRequerido;
        this.tipoCriterio = tipoCriterio;
        this.fuenteManual = fuenteManual;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTrastornoId() { return trastornoId; }
    public void setTrastornoId(Integer trastornoId) { this.trastornoId = trastornoId; }

    public String getIdentificadorCriterio() { return identificadorCriterio; }
    public void setIdentificadorCriterio(String identificadorCriterio) { this.identificadorCriterio = identificadorCriterio; }

    public String getDescripcionClinica() { return descripcionClinica; }
    public void setDescripcionClinica(String descripcionClinica) { this.descripcionClinica = descripcionClinica; }

    public Integer getDuracionMinimaMeses() { return duracionMinimaMeses; }
    public void setDuracionMinimaMeses(Integer duracionMinimaMeses) { this.duracionMinimaMeses = duracionMinimaMeses; }

    public Integer getUmbralSintomasRequerido() { return umbralSintomasRequerido; }
    public void setUmbralSintomasRequerido(Integer umbralSintomasRequerido) { this.umbralSintomasRequerido = umbralSintomasRequerido; }

    public String getTipoCriterio() { return tipoCriterio; }
    public void setTipoCriterio(String tipoCriterio) { this.tipoCriterio = tipoCriterio; }

    public String getFuenteManual() { return fuenteManual; }
    public void setFuenteManual(String fuenteManual) { this.fuenteManual = fuenteManual; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
