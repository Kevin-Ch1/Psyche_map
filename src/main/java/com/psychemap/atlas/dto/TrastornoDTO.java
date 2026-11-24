package com.psychemap.atlas.dto;

import java.util.ArrayList;
import java.util.List;

public class TrastornoDTO {

    private int id;
    private String codigoClasificacion;
    private String nombreTrastorno;
    private String categoriaNosologica;
    private double edadInicioPromedio;
    private double prevalenciaEstimada;
    private final List<CriterioDTO> criterios = new ArrayList<>();
    private final List<SintomaDTO> sintomas = new ArrayList<>();

    public TrastornoDTO(int id, String codigoClasificacion, String nombreTrastorno,
                        String categoriaNosologica, double edadInicioPromedio, double prevalenciaEstimada) {
        this.id = id;
        this.codigoClasificacion = codigoClasificacion;
        this.nombreTrastorno = nombreTrastorno;
        this.categoriaNosologica = categoriaNosologica;
        this.edadInicioPromedio = edadInicioPromedio;
        this.prevalenciaEstimada = prevalenciaEstimada;
    }

    public int getId() {
        return id;
    }

    public String getCodigoClasificacion() {
        return codigoClasificacion;
    }

    public String getNombreTrastorno() {
        return nombreTrastorno;
    }

    public String getCategoriaNosologica() {
        return categoriaNosologica;
    }

    public double getEdadInicioPromedio() {
        return edadInicioPromedio;
    }

    public double getPrevalenciaEstimada() {
        return prevalenciaEstimada;
    }

    public List<CriterioDTO> getCriterios() {
        return criterios;
    }

    public List<SintomaDTO> getSintomas() {
        return sintomas;
    }
}
