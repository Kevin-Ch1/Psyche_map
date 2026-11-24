package com.psychemap.atlas.dao;

import com.psychemap.atlas.model.CriterioDiagnostico;
import com.psychemap.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CriterioDAOImpl implements CriterioDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    @Override
    public List<CriterioDiagnostico> listarPorTrastornoId(int trastornoId) throws SQLException {
        String sql = """
                SELECT id, trastorno_id, identificador_criterio, descripcion_clinica,
                       duracion_minima_meses, umbral_sintomas_requerido, tipo_criterio,
                       fuente_manual, creado_en, actualizado_en
                FROM CriterioDiagnostico
                WHERE trastorno_id = ?
                ORDER BY identificador_criterio ASC
                """;
        List<CriterioDiagnostico> lista = new ArrayList<>();
        try (Connection conn = db.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trastornoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public int crear(CriterioDiagnostico c) throws SQLException {
        String sql = """
                INSERT INTO CriterioDiagnostico (
                    trastorno_id, identificador_criterio, descripcion_clinica,
                    duracion_minima_meses, umbral_sintomas_requerido, tipo_criterio, fuente_manual
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (Connection conn = db.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getTrastornoId());
            ps.setString(2, c.getIdentificadorCriterio());
            ps.setString(3, c.getDescripcionClinica());
            ps.setObject(4, c.getDuracionMinimaMeses(), Types.INTEGER);
            ps.setObject(5, c.getUmbralSintomasRequerido(), Types.INTEGER);
            ps.setString(6, c.getTipoCriterio());
            ps.setString(7, c.getFuenteManual() != null ? c.getFuenteManual() : "DSM-5-TR");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("No se pudo insertar el criterio diagnóstico");
    }

    @Override
    public boolean actualizar(CriterioDiagnostico c) throws SQLException {
        String sql = """
                UPDATE CriterioDiagnostico
                SET identificador_criterio = ?,
                    descripcion_clinica = ?,
                    duracion_minima_meses = ?,
                    umbral_sintomas_requerido = ?,
                    tipo_criterio = ?,
                    fuente_manual = ?,
                    actualizado_en = CURRENT_TIMESTAMP
                WHERE id = ?
                """;
        try (Connection conn = db.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getIdentificadorCriterio());
            ps.setString(2, c.getDescripcionClinica());
            ps.setObject(3, c.getDuracionMinimaMeses(), Types.INTEGER);
            ps.setObject(4, c.getUmbralSintomasRequerido(), Types.INTEGER);
            ps.setString(5, c.getTipoCriterio());
            ps.setString(6, c.getFuenteManual());
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private CriterioDiagnostico mapear(ResultSet rs) throws SQLException {
        CriterioDiagnostico c = new CriterioDiagnostico();
        c.setId(rs.getInt("id"));
        c.setTrastornoId(rs.getInt("trastorno_id"));
        c.setIdentificadorCriterio(rs.getString("identificador_criterio"));
        c.setDescripcionClinica(rs.getString("descripcion_clinica"));
        int dur = rs.getInt("duracion_minima_meses");
        c.setDuracionMinimaMeses(rs.wasNull() ? null : dur);
        int umb = rs.getInt("umbral_sintomas_requerido");
        c.setUmbralSintomasRequerido(rs.wasNull() ? null : umb);
        c.setTipoCriterio(rs.getString("tipo_criterio"));
        c.setFuenteManual(rs.getString("fuente_manual"));
        Timestamp creado = rs.getTimestamp("creado_en");
        if (creado != null) c.setCreadoEn(creado.toLocalDateTime());
        Timestamp act = rs.getTimestamp("actualizado_en");
        if (act != null) c.setActualizadoEn(act.toLocalDateTime());
        return c;
    }
}
