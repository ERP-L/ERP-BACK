package com.app.erp.shared.security;
import java.util.Set;

/**
 * Servicio base de RBAC.
 * Hoy puedes usar hasAnyRole(...) para chequear por roles.
 * Mañana puedes implementar permissionsForCompanyRoles(...) consultando BD/SP.
 */

public interface RbacService {
    default boolean hasAnyRole(Set<Integer> userRoles, Set<Integer> allowed) {
        return userRoles != null && userRoles.stream().anyMatch(allowed::contains);
    }

    /** Hook para futuro: derivar permisos lógicos desde roles (BD/SP/cache). */
    default Set<String> permissionsForCompanyRoles(Set<Integer> roleIds) {
        return Set.of(); // implementación simple: sin permisos aún
    }
}
