package com.psychemap.security.dto;

public class UsuarioDTO {

    public static final String ROL_ADMIN = "Administrador";
    public static final String ROL_CLINICO = "PsicologoClinico";
    public static final String ROL_INVESTIGADOR = "Investigador";

    private int id;
    private String nombreUsuario;
    private String correoElectronico;
    private boolean cuentaBloqueada;
    private String nombreRol;
    private boolean permisoLectura;
    private boolean permisoEscritura;
    private boolean permisoExportacion;

    public UsuarioDTO(int id, String nombreUsuario, String correoElectronico, boolean cuentaBloqueada,
                      String nombreRol, boolean permisoLectura, boolean permisoEscritura, boolean permisoExportacion) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.cuentaBloqueada = cuentaBloqueada;
        this.nombreRol = nombreRol;
        this.permisoLectura = permisoLectura;
        this.permisoEscritura = permisoEscritura;
        this.permisoExportacion = permisoExportacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public boolean isCuentaBloqueada() {
        return cuentaBloqueada;
    }

    public void setCuentaBloqueada(boolean cuentaBloqueada) {
        this.cuentaBloqueada = cuentaBloqueada;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public boolean isPermisoLectura() {
        return permisoLectura;
    }

    public boolean isPermisoEscritura() {
        return permisoEscritura;
    }

    public boolean isPermisoExportacion() {
        return permisoExportacion;
    }
}
