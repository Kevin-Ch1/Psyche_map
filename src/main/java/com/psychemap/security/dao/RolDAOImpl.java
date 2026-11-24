package com.psychemap.security.dao;

import com.psychemap.config.DatabaseConnection;
import com.psychemap.security.exception.DataAccessException;
import com.psychemap.security.model.RolPermiso;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RolDAOImpl implements RolDAO {

    private final Connection connection;

    public RolDAOImpl() {
        this(DatabaseConnection.getInstance().getConnection());
    }

    public RolDAOImpl(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataAccessException("La conexión a la base de datos no está disponible.");
        }
        return connection;
    }

    @Override
    public RolPermiso guardar(RolPermiso rolPermiso) {
        String sql = "INSERT INTO RolPermiso (usuario_id, nombre_rol, permiso_lectura, permiso_escritura, permiso_exportacion, creado_en) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, rolPermiso.getUsuarioId());
            stmt.setString(2, rolPermiso.getNombreRol());
            stmt.setBoolean(3, rolPermiso.isPermisoLectura());
            stmt.setBoolean(4, rolPermiso.isPermisoEscritura());
            stmt.setBoolean(5, rolPermiso.isPermisoExportacion());
            stmt.setTimestamp(6, rolPermiso.getCreadoEn() != null ? Timestamp.valueOf(rolPermiso.getCreadoEn())
                    : Timestamp.valueOf(LocalDateTime.now()));

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("No se pudo insertar el rol en RolPermiso.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    rolPermiso.setId(keys.getInt(1));
                }
            }
            return rolPermiso;
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al guardar rol_permiso: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RolPermiso> buscarPorUsuarioId(Integer usuarioId) {
        String sql = "SELECT id, usuario_id, nombre_rol, permiso_lectura, permiso_escritura, permiso_exportacion, creado_en "
                + "FROM RolPermiso WHERE usuario_id = ? ORDER BY id ASC";
        List<RolPermiso> roles = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(mapearRolPermiso(rs));
                }
            }
            return roles;
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al buscar roles de usuario: " + e.getMessage(), e);
        }
    }

    private RolPermiso mapearRolPermiso(ResultSet rs) throws SQLException {
        Timestamp creadoEnTs = rs.getTimestamp("creado_en");
        return new RolPermiso(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                rs.getString("nombre_rol"),
                rs.getBoolean("permiso_lectura"),
                rs.getBoolean("permiso_escritura"),
                rs.getBoolean("permiso_exportacion"),
                creadoEnTs != null ? creadoEnTs.toLocalDateTime() : LocalDateTime.now());
    }
}
