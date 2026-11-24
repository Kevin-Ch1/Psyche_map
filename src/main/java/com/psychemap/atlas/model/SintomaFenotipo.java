package com.psychemap.atlas.model;

import java.time.LocalDateTime;

public class SintomaFenotipo {
    private Integer id;
    private String nombreSintoma;
    private String descripcionFenomenologica;
    private String dominioAfectado;
    private boolean cardinal;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public SintomaFenotipo() {
    }

    public SintomaFenotipo(Integer id, String nombreSintoma, String descripcionFenomenologica,
                           String dominioAfectado, boolean cardinal) {
        this.id = id;
        this.nombreSintoma = nombreSintoma;
        this.descripcionFenomenologica = descripcionFenomenologica;
        this.dominioAfectado = dominioAfectado;
        this.cardinal = cardinal;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombreSintoma() { return nombreSintoma; }
    public void setNombreSintoma(String nombreSintoma) { this.nombreSintoma = nombreSintoma; }

    public String getDescripcionFenomenologica() { return descripcionFenomenologica; }
    public void setDescripcionFenomenologica(String descripcionFenomenologica) { this.descripcionFenomenologica = descripcionFenomenologica; }

    public String getDominioAfectado() { return dominioAfectado; }
    public void setDominioAfectado(String dominioAfectado) { this.dominioAfectado = dominioAfectado; }

    public boolean isCardinal() { return cardinal; }
    public void setCardinal(boolean cardinal) { this.cardinal = cardinal; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
