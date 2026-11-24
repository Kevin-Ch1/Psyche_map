package com.psychemap.atlas.dao;

import com.psychemap.atlas.model.SintomaFenotipo;

import java.sql.SQLException;
import java.util.List;

public interface SintomaDAO {

    List<SintomaFenotipo> buscarPorTrastornoId(int trastornoId) throws SQLException;
}
