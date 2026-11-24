package com.psychemap.security.service;

import com.psychemap.security.model.RolPermiso;
import com.psychemap.security.model.Usuario;

import java.util.List;

public class SessionManager {

    private static final SessionManager INSTANCIA = new SessionManager();
    private Usuario usuarioActual;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCIA;
    }

    public synchronized void iniciarSesion(Usuario usuario, List<RolPermiso> roles) {
        if (usuario == null) {
            throw new IllegalArgumentException("No se puede iniciar sesión con un usuario nulo.");
        }
        this.usuarioActual = usuario;
    }

    public Integer getUsuarioIdActual() {
        return (this.usuarioActual != null) ? this.usuarioActual.getId() : null;
    }
}
