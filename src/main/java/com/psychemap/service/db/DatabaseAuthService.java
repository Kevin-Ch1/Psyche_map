package com.psychemap.service.db;

import com.psychemap.audit.dao.AuditLogDAO;
import com.psychemap.audit.dao.AuditLogDAOImpl;
import com.psychemap.audit.model.LogAuditoriaAcceso;
import com.psychemap.audit.service.NetworkUtil;
import com.psychemap.security.dto.UsuarioDTO;
import com.psychemap.security.crypto.PBKDF2PasswordHasher;
import com.psychemap.security.crypto.PasswordHasher;
import com.psychemap.security.dao.RolDAO;
import com.psychemap.security.dao.RolDAOImpl;
import com.psychemap.security.dao.UsuarioDAO;
import com.psychemap.security.dao.UsuarioDAOImpl;
import com.psychemap.security.model.RolNombre;
import com.psychemap.security.model.RolPermiso;
import com.psychemap.security.model.Usuario;
import com.psychemap.security.service.SessionManager;
import com.psychemap.service.AuthException;
import com.psychemap.service.IAuthService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DatabaseAuthService implements IAuthService {

    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;
    private final AuditLogDAO auditLogDAO;
    private final PasswordHasher hasher;

    public DatabaseAuthService() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.rolDAO = new RolDAOImpl();
        this.auditLogDAO = new AuditLogDAOImpl();
        this.hasher = new PBKDF2PasswordHasher();
        inicializarUsuariosPorDefecto();
    }

    private void inicializarUsuariosPorDefecto() {
        try {
            if (usuarioDAO.buscarPorNombreUsuario("admin").isEmpty()) {
                crearUsuarioEnBD("admin", "admin1234", "admin@psychemap.local",
                        RolNombre.ADMINISTRADOR_TI, true, true, true);
            }
            if (usuarioDAO.buscarPorNombreUsuario("dra.ramirez").isEmpty()) {
                crearUsuarioEnBD("dra.ramirez", "demo1234", "e.ramirez@psychemap.local",
                        RolNombre.PSICOLOGO, true, false, true);
            }
            if (usuarioDAO.buscarPorNombreUsuario("inv.paredes").isEmpty()) {
                crearUsuarioEnBD("inv.paredes", "demo1234", "r.paredes@psychemap.local",
                        RolNombre.INVESTIGADOR, true, true, false);
            }
        } catch (Exception e) {
            System.err.println("[DB AuthService] Nota: No se pudo verificar/insertar usuarios iniciales: " + e.getMessage());
        }
    }

    private void crearUsuarioEnBD(String username, String rawPassword, String email,
                                 RolNombre rol, boolean lectura, boolean escritura, boolean exportacion) {
        String hash = hasher.hash(rawPassword);
        Usuario u = new Usuario(username, hash, email);
        u = usuarioDAO.guardar(u);
        if (u.getId() != null) {
            RolPermiso rp = new RolPermiso(u.getId(), rol.name(), lectura, escritura, exportacion);
            rolDAO.guardar(rp);
        }
    }

    @Override
    public UsuarioDTO login(String nombreUsuario, String contrasena) throws AuthException {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.isEmpty()) {
            throw new AuthException("Ingresa usuario y contraseña.");
        }

        String ipLocal = NetworkUtil.getLocalIpAddress();
        Optional<Usuario> optUser = usuarioDAO.buscarPorNombreUsuario(nombreUsuario.trim());

        if (optUser.isEmpty()) {
            registrarTrazaSeguridad(null, ipLocal, "LOGIN_FALLIDO_USUARIO_INEXISTENTE: " + nombreUsuario);
            throw new AuthException("Usuario o contraseña incorrectos.");
        }

        Usuario usuario = optUser.get();

        if (usuario.isCuentaBloqueada()) {
            registrarTrazaSeguridad(usuario.getId(), ipLocal, "LOGIN_RECHAZADO_CUENTA_BLOQUEADA");
            throw new AuthException("La cuenta está bloqueada. Consulta al administrador.");
        }

        if (!hasher.verify(contrasena, usuario.getHashContrasenia())) {
            registrarTrazaSeguridad(usuario.getId(), ipLocal, "LOGIN_FALLIDO_CONTRASENIA_ERRONEA");
            throw new AuthException("Usuario o contraseña incorrectos.");
        }

        usuarioDAO.actualizarUltimoAcceso(usuario.getId(), LocalDateTime.now());

        List<RolPermiso> roles = rolDAO.buscarPorUsuarioId(usuario.getId());

        SessionManager.getInstance().iniciarSesion(usuario, roles);

        registrarTrazaSeguridad(usuario.getId(), ipLocal, "INICIO_SESION_EXITOSO");

        return mapearADTO(usuario, roles);
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        List<UsuarioDTO> dtos = new ArrayList<>();
        for (Usuario u : usuarios) {
            List<RolPermiso> roles = rolDAO.buscarPorUsuarioId(u.getId());
            dtos.add(mapearADTO(u, roles));
        }
        return dtos;
    }

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO nuevo, String contrasenaInicial) {
        if (nuevo.getNombreUsuario() == null || nuevo.getNombreUsuario().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (contrasenaInicial == null || contrasenaInicial.isBlank()) {
            throw new IllegalArgumentException("La contraseña inicial es obligatoria.");
        }
        if (usuarioDAO.buscarPorNombreUsuario(nuevo.getNombreUsuario().trim()).isPresent()) {
            throw new IllegalArgumentException("Ese nombre de usuario ya existe en la base de datos.");
        }

        String hash = hasher.hash(contrasenaInicial);
        Usuario usuario = new Usuario(nuevo.getNombreUsuario().trim(), hash, nuevo.getCorreoElectronico().trim());
        usuario.setCuentaBloqueada(false);
        usuario = usuarioDAO.guardar(usuario);

        RolNombre rolNombre = mapearRolEnum(nuevo.getNombreRol());
        RolPermiso rp = new RolPermiso(usuario.getId(), rolNombre.name(),
                nuevo.isPermisoLectura(), nuevo.isPermisoEscritura(), nuevo.isPermisoExportacion());
        rolDAO.guardar(rp);

        registrarTrazaSeguridad(SessionManager.getInstance().getUsuarioIdActual(),
                NetworkUtil.getLocalIpAddress(), "CREAR_USUARIO: " + usuario.getNombreUsuario());

        return mapearADTO(usuario, Collections.singletonList(rp));
    }

    @Override
    public void cambiarBloqueo(int idUsuario, boolean bloqueada) {
        usuarioDAO.cambiarBloqueoCuenta(idUsuario, bloqueada);
        registrarTrazaSeguridad(SessionManager.getInstance().getUsuarioIdActual(),
                NetworkUtil.getLocalIpAddress(), (bloqueada ? "SUSPENDER_CUENTA_ID: " : "REACTIVAR_CUENTA_ID: ") + idUsuario);
    }

    @Override
    public void reiniciarAcceso(int idUsuario) {
        String hashTemporal = hasher.hash("cambiar123");
        usuarioDAO.buscarPorId(idUsuario).ifPresent(u -> {
            u.setHashContrasenia(hashTemporal);
            usuarioDAO.actualizar(u);
        });
        registrarTrazaSeguridad(SessionManager.getInstance().getUsuarioIdActual(),
                NetworkUtil.getLocalIpAddress(), "REINICIAR_ACCESO_ID: " + idUsuario);
    }

    private void registrarTrazaSeguridad(Integer usuarioId, String ip, String accion) {
        try {
            Integer uid = usuarioId != null ? usuarioId : 1;
            LogAuditoriaAcceso log = new LogAuditoriaAcceso(uid, ip, "SEGURIDAD", accion, "UsuarioSistema");
            auditLogDAO.registrarLog(log);
        } catch (Exception e) {
            System.err.println("[DB Audit] Advertencia: " + e.getMessage());
        }
    }

    private UsuarioDTO mapearADTO(Usuario u, List<RolPermiso> roles) {
        String rolAmigable = UsuarioDTO.ROL_CLINICO;
        boolean lectura = true;
        boolean escritura = false;
        boolean exportacion = false;

        if (!roles.isEmpty()) {
            RolPermiso principal = roles.get(0);
            rolAmigable = switch (principal.getRolEnum()) {
                case ADMINISTRADOR_TI -> UsuarioDTO.ROL_ADMIN;
                case INVESTIGADOR -> UsuarioDTO.ROL_INVESTIGADOR;
                default -> UsuarioDTO.ROL_CLINICO;
            };
            lectura = roles.stream().anyMatch(RolPermiso::isPermisoLectura);
            escritura = roles.stream().anyMatch(RolPermiso::isPermisoEscritura);
            exportacion = roles.stream().anyMatch(RolPermiso::isPermisoExportacion);
        }

        return new UsuarioDTO(u.getId(), u.getNombreUsuario(), u.getCorreoElectronico(),
                u.isCuentaBloqueada(), rolAmigable, lectura, escritura, exportacion);
    }

    private RolNombre mapearRolEnum(String rolTexto) {
        if (rolTexto == null) return RolNombre.PSICOLOGO;
        return switch (rolTexto) {
            case UsuarioDTO.ROL_ADMIN -> RolNombre.ADMINISTRADOR_TI;
            case UsuarioDTO.ROL_INVESTIGADOR -> RolNombre.INVESTIGADOR;
            default -> RolNombre.PSICOLOGO;
        };
    }
}
