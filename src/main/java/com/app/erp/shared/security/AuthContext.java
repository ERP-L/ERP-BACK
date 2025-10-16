package com.app.erp.shared.security;

import java.util.Set;

/** Contexto de autenticación derivado del JWT (reutilizable en todos los BCs). */

public record AuthContext(
        int userId,               // sid (security user id) - 0 si faltara
        Integer companyId,        // cid - null si no aplica
        Set<Integer> rolesCompany, // roles_company (IDs por compañía)
        Set<Integer> rolesGlobal   // roles_global (IDs globales)
) {
    public boolean hasAnyCompanyRole(Set<Integer> allowed) {
        return rolesCompany != null && rolesCompany.stream().anyMatch(allowed::contains);
    }
}
