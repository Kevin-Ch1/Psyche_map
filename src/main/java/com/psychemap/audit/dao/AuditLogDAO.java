package com.psychemap.audit.dao;

import com.psychemap.audit.model.LogAuditoriaAcceso;
import com.psychemap.audit.dto.AuditLogDTO;

import java.util.List;

public interface AuditLogDAO {

    LogAuditoriaAcceso registrarLog(LogAuditoriaAcceso log);

    List<AuditLogDTO> listarUltimos(int limite);
}
