package com.psychemap.service;

import com.psychemap.atlas.dto.CriterioDTO;
import com.psychemap.atlas.dto.TrastornoDTO;

import java.util.List;

public interface IAtlasService {

    List<TrastornoDTO> listarTrastornos();

    List<TrastornoDTO> buscar(String texto);

    void guardarCriterio(int idTrastorno, CriterioDTO criterio);
}
