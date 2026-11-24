package com.psychemap.audit.dao;

import com.psychemap.audit.model.LogAuditoriaAcceso;
import com.psychemap.config.DatabaseConnection;
import com.psychemap.audit.dto.AuditLogDTO;
import com.psychemap.security.exception.DataAccessException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements AuditLogDAO {

    private final Connection connection;

    public AuditLogDAOImpl() {
        this(DatabaseConnection.getInstance().getConnection());
    }

    public AuditLogDAOImpl(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataAccessException("La conexión a la base de datos no está disponible.");
        }
        return connection;
    }

    @Override
    public LogAuditoriaAcceso registrarLog(LogAuditoriaAcceso log) {
        String sql = "INSERT INTO LogAuditoriaAcceso (usuario_id, direccion_ip, modulo_consultado, accion_realizada, entidad_afectada, fecha_hora_exacta) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, log.getUsuarioId());
            stmt.setString(2, log.getDireccionIp());
            stmt.setString(3, log.getModuloConsultado());
            stmt.setString(4, log.getAccionRealizada());
            stmt.setString(5, log.getEntidadAfectada());
            stmt.setTimestamp(6, log.getFechaHoraExacta() != null ? Timestamp.valueOf(log.getFechaHoraExacta())
                    : Timestamp.valueOf(LocalDateTime.now()));

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("No se pudo insertar el registro en LogAuditoriaAcceso.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    log.setId(keys.getInt(1));
                }
            }
            return log;
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al registrar log de auditoría: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLogDTO> listarUltimos(int limite) {
        String sql = "SELECT l.id, u.nombre_usuario, l.direccion_ip, l.modulo_consultado, " +
                "l.accion_realizada, l.entidad_afectada, l.fecha_hora_exacta " +
                "FROM LogAuditoriaAcceso l " +
                "LEFT JOIN UsuarioSistema u ON l.usuario_id = u.id " +
                "ORDER BY l.fecha_hora_exacta DESC LIMIT ?";
        List<AuditLogDTO> resultados = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapearAuditLogDTO(rs));
                }
            }
            return resultados;
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al listar últimos registros de auditoría: " + e.getMessage(), e);
        }
    }

    private AuditLogDTO mapearAuditLogDTO(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fecha_hora_exacta");
        return new AuditLogDTO(
                rs.getInt("id"),
                rs.getString("nombre_usuario") != null ? rs.getString("nombre_usuario") : "SISTEMA/DESCONOCIDO",
                rs.getString("direccion_ip"),
                rs.getString("modulo_consultado"),
                rs.getString("accion_realizada"),
                rs.getString("entidad_afectada"),
                ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
    }
}
