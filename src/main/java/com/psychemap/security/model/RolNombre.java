package com.psychemap.security.model;

public enum RolNombre {
    PSICOLOGO,
    INVESTIGADOR,
    ADMINISTRADOR_TI;

    public static RolNombre fromString(String rolStr) {
        if (rolStr == null || rolStr.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol no puede ser nulo ni vacío.");
        }
        String clean = rolStr.trim().toUpperCase();
        return switch (clean) {
            case "ADMINISTRADOR", "ADMINISTRADOR_TI", "ADMIN" -> ADMINISTRADOR_TI;
            case "PSICOLOGOCLINICO", "PSICOLOGO_CLINICO", "PSICOLOGO" -> PSICOLOGO;
            case "INVESTIGADOR" -> INVESTIGADOR;
            default -> {
                try {
                    yield RolNombre.valueOf(clean);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Rol desconocido en el sistema: " + rolStr);
                }
            }
        };
    }
}
