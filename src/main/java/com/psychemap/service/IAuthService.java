package com.psychemap.service;

import com.psychemap.security.dto.UsuarioDTO;

import java.util.List;

public interface IAuthService {

    UsuarioDTO login(String nombreUsuario, String contrasena) throws AuthException;

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO crearUsuario(UsuarioDTO nuevo, String contrasenaInicial);

    void cambiarBloqueo(int idUsuario, boolean bloqueada);

    void reiniciarAcceso(int idUsuario);
}
