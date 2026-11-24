package com.psychemap.service.db;

import com.psychemap.audit.dao.AuditLogDAO;
import com.psychemap.audit.dao.AuditLogDAOImpl;
import com.psychemap.audit.model.LogAuditoriaAcceso;
import com.psychemap.audit.dto.AuditLogDTO;
import com.psychemap.security.dao.UsuarioDAO;
import com.psychemap.security.dao.UsuarioDAOImpl;
import com.psychemap.security.model.Usuario;
import com.psychemap.security.service.SessionManager;
import com.psychemap.service.IAuditService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAuditService implements IAuditService {

    private final AuditLogDAO auditLogDAO;
    private final UsuarioDAO usuarioDAO;

    public DatabaseAuditService() {
        this.auditLogDAO = new AuditLogDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    @Override
    public void registrar(String nombreUsuario, String direccionIp, String modulo, String accion, String entidad) {
        Integer usuarioId = SessionManager.getInstance().getUsuarioIdActual();
        if (usuarioId == null && nombreUsuario != null) {
            usuarioId = usuarioDAO.buscarPorNombreUsuario(nombreUsuario.trim())
                    .map(Usuario::getId)
                    .orElse(1);
        }
        if (usuarioId == null) {
            usuarioId = 1;
        }

        LogAuditoriaAcceso log = new LogAuditoriaAcceso(usuarioId, direccionIp, modulo, accion, entidad);
        log.setFechaHoraExacta(LocalDateTime.now());
        try {
            auditLogDAO.registrarLog(log);
        } catch (Exception e) {
            System.err.println("[DB AuditService] Error al escribir en bitácora inmutable: " + e.getMessage());
        }
    }

    @Override
    public List<AuditLogDTO> listarRegistros() {
        try {
            return auditLogDAO.listarUltimos(300);
        } catch (Exception e) {
            System.err.println("[DB AuditService] Error al leer bitácora de PostgreSQL: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
