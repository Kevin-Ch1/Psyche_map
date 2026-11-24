package com.psychemap.service;

import com.psychemap.audit.dto.AuditLogDTO;

import java.util.List;

public interface IAuditService {

    void registrar(String nombreUsuario, String direccionIp, String modulo,
                   String accion, String entidad);

    List<AuditLogDTO> listarRegistros();
}
