package com.psychemap.atlas.dao;

import com.psychemap.atlas.model.CriterioDiagnostico;

import java.sql.SQLException;
import java.util.List;

public interface CriterioDAO {

    List<CriterioDiagnostico> listarPorTrastornoId(int trastornoId) throws SQLException;

    int crear(CriterioDiagnostico c) throws SQLException;

    boolean actualizar(CriterioDiagnostico c) throws SQLException;
}
