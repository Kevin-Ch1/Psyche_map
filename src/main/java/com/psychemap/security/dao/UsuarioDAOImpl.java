package com.psychemap.security.dao;

import com.psychemap.config.DatabaseConnection;
import com.psychemap.security.exception.DataAccessException;
import com.psychemap.security.model.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    private final Connection connection;

    public UsuarioDAOImpl() {
        this(DatabaseConnection.getInstance().getConnection());
    }

    public UsuarioDAOImpl(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataAccessException("La conexión a la base de datos no está disponible.");
        }
        return connection;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        String sql = "INSERT INTO UsuarioSistema (nombre_usuario, hash_contrasenia, correo_electronico, cuenta_bloqueada, ultimo_acceso, creado_en, estado_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getHashContrasenia());
            stmt.setString(3, usuario.getCorreoElectronico());
            stmt.setBoolean(4, usuario.isCuentaBloqueada());
            stmt.setTimestamp(5,
                    usuario.getUltimoAcceso() != null ? Timestamp.valueOf(usuario.getUltimoAcceso()) : null);
            stmt.setTimestamp(6, usuario.getCreadoEn() != null ? Timestamp.valueOf(usuario.getCreadoEn())
                    : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(7, usuario.getEstadoRegistro() != null ? usuario.getEstadoRegistro() : "Activo");

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("No se pudo crear el usuario en UsuarioSistema.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Fallo al obtener el ID generado para UsuarioSistema.");
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al guardar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        String sql = "SELECT id, nombre_usuario, hash_contrasenia, correo_electronico, cuenta_bloqueada, ultimo_acceso, creado_en, estado_registro "
                + "FROM UsuarioSistema WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al buscar usuario por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT id, nombre_usuario, hash_contrasenia, correo_electronico, cuenta_bloqueada, ultimo_acceso, creado_en, estado_registro "
                + "FROM UsuarioSistema WHERE nombre_usuario = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al buscar por nombre_usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT id, nombre_usuario, hash_contrasenia, correo_electronico, cuenta_bloqueada, ultimo_acceso, creado_en, estado_registro "
                + "FROM UsuarioSistema ORDER BY id ASC";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al listar usuarios: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE UsuarioSistema SET nombre_usuario = ?, hash_contrasenia = ?, correo_electronico = ?, " +
                "cuenta_bloqueada = ?, ultimo_acceso = ?, estado_registro = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getHashContrasenia());
            stmt.setString(3, usuario.getCorreoElectronico());
            stmt.setBoolean(4, usuario.isCuentaBloqueada());
            stmt.setTimestamp(5,
                    usuario.getUltimoAcceso() != null ? Timestamp.valueOf(usuario.getUltimoAcceso()) : null);
            stmt.setString(6, usuario.getEstadoRegistro());
            stmt.setInt(7, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarUltimoAcceso(Integer usuarioId, LocalDateTime fechaHora) {
        String sql = "UPDATE UsuarioSistema SET ultimo_acceso = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(fechaHora));
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al actualizar ultimo_acceso: " + e.getMessage(), e);
        }
    }

    @Override
    public void cambiarBloqueoCuenta(Integer usuarioId, boolean bloqueada) {
        String sql = "UPDATE UsuarioSistema SET cuenta_bloqueada = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, bloqueada);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error SQL al cambiar bloqueo de cuenta: " + e.getMessage(), e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Timestamp ultimoAccesoTs = rs.getTimestamp("ultimo_acceso");
        Timestamp creadoEnTs = rs.getTimestamp("creado_en");
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre_usuario"),
                rs.getString("hash_contrasenia"),
                rs.getString("correo_electronico"),
                rs.getBoolean("cuenta_bloqueada"),
                ultimoAccesoTs != null ? ultimoAccesoTs.toLocalDateTime() : null,
                creadoEnTs != null ? creadoEnTs.toLocalDateTime() : LocalDateTime.now(),
                rs.getString("estado_registro"));
    }
}
