package com.psychemap.atlas.dao;

import com.psychemap.atlas.model.TrastornoClinico;

import java.sql.SQLException;
import java.util.List;

public interface TrastornoDAO {

    TrastornoClinico guardar(TrastornoClinico trastorno) throws SQLException;

    List<TrastornoClinico> listarTodos() throws SQLException;

    List<TrastornoClinico> buscarPorTermino(String termino) throws SQLException;
}
