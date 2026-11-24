package com.psychemap.atlas.dto;

public class SintomaDTO {

    private Integer id;
    private String nombreSintoma;
    private String descripcionFenomenologica;
    private String dominioAfectado;
    private boolean esCardinal;

    public SintomaDTO(String nombreSintoma, String descripcionFenomenologica, String dominioAfectado, boolean esCardinal) {
        this(null, nombreSintoma, descripcionFenomenologica, dominioAfectado, esCardinal);
    }

    public SintomaDTO(Integer id, String nombreSintoma, String descripcionFenomenologica, String dominioAfectado, boolean esCardinal) {
        this.id = id;
        this.nombreSintoma = nombreSintoma;
        this.descripcionFenomenologica = descripcionFenomenologica;
        this.dominioAfectado = dominioAfectado;
        this.esCardinal = esCardinal;
    }

    public Integer getId() {
        return id;
    }

    public String getNombreSintoma() {
        return nombreSintoma;
    }

    public String getDescripcionFenomenologica() {
        return descripcionFenomenologica;
    }

    public String getDominioAfectado() {
        return dominioAfectado;
    }

    public boolean isEsCardinal() {
        return esCardinal;
    }
}
