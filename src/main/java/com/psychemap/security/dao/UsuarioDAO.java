package com.psychemap.security.dao;

import com.psychemap.security.model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Integer id);

    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    List<Usuario> listarTodos();

    void actualizar(Usuario usuario);

    void actualizarUltimoAcceso(Integer usuarioId, LocalDateTime fechaHora);

    void cambiarBloqueoCuenta(Integer usuarioId, boolean bloqueada);
}
