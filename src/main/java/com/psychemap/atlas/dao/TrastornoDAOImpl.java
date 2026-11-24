package com.psychemap.atlas.dao;

import com.psychemap.atlas.model.TrastornoClinico;
import com.psychemap.config.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrastornoDAOImpl implements TrastornoDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    @Override
    public TrastornoClinico guardar(TrastornoClinico t) throws SQLException {
        String sql = """
                INSERT INTO TrastornoClinico (
                    codigo_clasificacion, nombre_trastorno, categoria_nosologica,
                    edad_inicio_promedio, prevalencia_estimada, creado_en, estado_registro
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (Connection conn = db.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getCodigoClasificacion());
            stmt.setString(2, t.getNombreTrastorno());
            stmt.setString(3, t.getCategoriaNosologica());
            stmt.setString(4, t.getEdadInicioPromedio());
            stmt.setString(5, t.getPrevalenciaEstimada());
            stmt.setTimestamp(6, Timestamp.valueOf(t.getCreadoEn() != null ? t.getCreadoEn() : LocalDateTime.now()));
            stmt.setString(7, t.getEstadoRegistro() != null ? t.getEstadoRegistro() : "Activo");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
            }
            return t;
        }
    }

    @Override
    public List<TrastornoClinico> listarTodos() throws SQLException {
        String sql = """
                SELECT id, codigo_clasificacion, nombre_trastorno, categoria_nosologica,
                       edad_inicio_promedio, prevalencia_estimada, creado_en, actualizado_en, estado_registro
                FROM TrastornoClinico WHERE estado_registro = 'Activo' ORDER BY codigo_clasificacion ASC
                """;
        List<TrastornoClinico> lista = new ArrayList<>();
        try (Connection conn = db.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public List<TrastornoClinico> buscarPorTermino(String termino) throws SQLException {
        String sql = """
                SELECT DISTINCT t.id, t.codigo_clasificacion, t.nombre_trastorno,
                       t.categoria_nosologica, t.edad_inicio_promedio, t.prevalencia_estimada,
                       t.creado_en, t.actualizado_en, t.estado_registro
                FROM TrastornoClinico t
                LEFT JOIN CriterioDiagnostico c ON c.trastorno_id = t.id
                WHERE t.estado_registro = 'Activo'
                  AND (
                       ? IS NULL OR TRIM(?) = ''
                       OR LOWER(t.codigo_clasificacion) LIKE LOWER(?)
                       OR LOWER(t.nombre_trastorno) LIKE LOWER(?)
                       OR LOWER(c.descripcion_clinica) LIKE LOWER(?)
                  )
                ORDER BY t.codigo_clasificacion ASC
                """;
        String patron = "%" + (termino == null ? "" : termino.trim()) + "%";
        List<TrastornoClinico> resultados = new ArrayList<>();

        try (Connection conn = db.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, termino);
            ps.setString(2, termino);
            ps.setString(3, patron);
            ps.setString(4, patron);
            ps.setString(5, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapear(rs));
                }
            }
        }
        return resultados;
    }

    private TrastornoClinico mapear(ResultSet rs) throws SQLException {
        Timestamp creadoEn = rs.getTimestamp("creado_en");
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        return new TrastornoClinico(
                rs.getInt("id"),
                rs.getString("codigo_clasificacion"),
                rs.getString("nombre_trastorno"),
                rs.getString("categoria_nosologica"),
                rs.getString("edad_inicio_promedio"),
                rs.getString("prevalencia_estimada"),
                creadoEn != null ? creadoEn.toLocalDateTime() : null,
                actualizadoEn != null ? actualizadoEn.toLocalDateTime() : null,
                rs.getString("estado_registro")
        );
    }
}
