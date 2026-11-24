package com.psychemap.security.model;

import java.time.LocalDateTime;

public class Usuario {

    private Integer id;
    private String nombreUsuario;
    private String hashContrasenia;
    private String correoElectronico;
    private boolean cuentaBloqueada;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime creadoEn;
    private String estadoRegistro;

    public Usuario() {
        this.cuentaBloqueada = false;
        this.creadoEn = LocalDateTime.now();
        this.estadoRegistro = "Activo";
    }

    public Usuario(String nombreUsuario, String hashContrasenia, String correoElectronico) {
        this();
        this.nombreUsuario = nombreUsuario;
        this.hashContrasenia = hashContrasenia;
        this.correoElectronico = correoElectronico;
    }

    public Usuario(Integer id, String nombreUsuario, String hashContrasenia, String correoElectronico,
            boolean cuentaBloqueada, LocalDateTime ultimoAcceso, LocalDateTime creadoEn, String estadoRegistro) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.hashContrasenia = hashContrasenia;
        this.correoElectronico = correoElectronico;
        this.cuentaBloqueada = cuentaBloqueada;
        this.ultimoAcceso = ultimoAcceso;
        this.creadoEn = creadoEn;
        this.estadoRegistro = estadoRegistro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getHashContrasenia() {
        return hashContrasenia;
    }

    public void setHashContrasenia(String hashContrasenia) {
        this.hashContrasenia = hashContrasenia;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public boolean isCuentaBloqueada() {
        return cuentaBloqueada;
    }

    public void setCuentaBloqueada(boolean cuentaBloqueada) {
        this.cuentaBloqueada = cuentaBloqueada;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(String estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }
}
