package com.psychemap.atlas.dto;

public class CriterioDTO {

    private Integer id;
    private Integer trastornoId;
    private String identificadorCriterio;
    private String descripcionClinica;
    private int duracionMinimaMeses;
    private int umbralSintomasRequerido;
    private String tipoCriterio;
    private String fuenteManual;

    public CriterioDTO(String identificadorCriterio, String descripcionClinica,
                       int duracionMinimaMeses, int umbralSintomasRequerido, String tipoCriterio) {
        this(null, null, identificadorCriterio, descripcionClinica, duracionMinimaMeses, umbralSintomasRequerido, tipoCriterio, "DSM-5-TR");
    }

    public CriterioDTO(Integer id, Integer trastornoId, String identificadorCriterio,
                       String descripcionClinica, Integer duracionMinimaMeses,
                       Integer umbralSintomasRequerido, String tipoCriterio, String fuenteManual) {
        this.id = id;
        this.trastornoId = trastornoId;
        this.identificadorCriterio = identificadorCriterio;
        this.descripcionClinica = descripcionClinica;
        this.duracionMinimaMeses = duracionMinimaMeses != null ? duracionMinimaMeses : 0;
        this.umbralSintomasRequerido = umbralSintomasRequerido != null ? umbralSintomasRequerido : 0;
        this.tipoCriterio = tipoCriterio;
        this.fuenteManual = fuenteManual != null ? fuenteManual : "DSM-5-TR";
    }

    public Integer getId() {
        return id;
    }

    public Integer getTrastornoId() {
        return trastornoId;
    }

    public String getIdentificadorCriterio() {
        return identificadorCriterio;
    }

    public String getDescripcionClinica() {
        return descripcionClinica;
    }

    public int getDuracionMinimaMeses() {
        return duracionMinimaMeses;
    }

    public int getUmbralSintomasRequerido() {
        return umbralSintomasRequerido;
    }

    public String getTipoCriterio() {
        return tipoCriterio;
    }

    public String getFuenteManual() {
        return fuenteManual;
    }
}
