package com.psychemap.atlas.dao;

import com.psychemap.atlas.model.SintomaFenotipo;
import com.psychemap.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SintomaDAOImpl implements SintomaDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    @Override
    public List<SintomaFenotipo> buscarPorTrastornoId(int trastornoId) throws SQLException {
        String sql = """
                SELECT s.id, s.nombre_sintoma, s.descripcion_fenomenologica,
                       s.dominio_afectado, s.es_cardinal, s.creado_en, s.actualizado_en
                FROM SintomaFenotipo s
                JOIN TrastornoSintoma ts ON ts.sintoma_id = s.id
                WHERE ts.trastorno_id = ?
                ORDER BY s.es_cardinal DESC, s.nombre_sintoma ASC
                """;
        List<SintomaFenotipo> sintomas = new ArrayList<>();
        try (Connection conn = db.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trastornoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sintomas.add(mapear(rs));
                }
            }
        }
        return sintomas;
    }

    private SintomaFenotipo mapear(ResultSet rs) throws SQLException {
        SintomaFenotipo s = new SintomaFenotipo();
        s.setId(rs.getInt("id"));
        s.setNombreSintoma(rs.getString("nombre_sintoma"));
        s.setDescripcionFenomenologica(rs.getString("descripcion_fenomenologica"));
        s.setDominioAfectado(rs.getString("dominio_afectado"));
        s.setCardinal(rs.getBoolean("es_cardinal"));
        Timestamp creado = rs.getTimestamp("creado_en");
        if (creado != null) s.setCreadoEn(creado.toLocalDateTime());
        Timestamp act = rs.getTimestamp("actualizado_en");
        if (act != null) s.setActualizadoEn(act.toLocalDateTime());
        return s;
    }
}
