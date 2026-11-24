package com.psychemap.security.dao;

import com.psychemap.security.model.RolPermiso;
import java.util.List;

public interface RolDAO {

    RolPermiso guardar(RolPermiso rolPermiso);

    List<RolPermiso> buscarPorUsuarioId(Integer usuarioId);
}
