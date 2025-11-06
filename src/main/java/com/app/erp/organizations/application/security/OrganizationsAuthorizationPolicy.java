package com.app.erp.organizations.application.security;

import com.app.erp.shared.exceptions.AuthorizationException;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.RbacService;

import java.util.Objects;
import java.util.Set;

/** Reglas de autorización del BC Organizations. */
public final class OrganizationsAuthorizationPolicy {

    private final Set<Integer> allowedCreateBranchRoles;
    private final RbacService rbac;

    public OrganizationsAuthorizationPolicy(Set<Integer> allowedCreateBranchRoles, RbacService rbac) {
        this.allowedCreateBranchRoles = Objects.requireNonNull(allowedCreateBranchRoles);
        this.rbac = Objects.requireNonNull(rbac);
    }

    /** Hoy: autoriza solo por roles_company. Mañana puedes añadir scope por companyId o permisos. */
    public void assertCanCreateBranch(AuthContext ctx) {
        if (ctx == null) throw new AuthorizationException("No autorizado (sin contexto)");
        boolean ok = rbac.hasAnyRole(ctx.rolesCompany(), allowedCreateBranchRoles);
        if (!ok) throw new AuthorizationException("No tienes rol para crear sucursales");
    }

    // Ejemplos para crecer (los irás usando en otros endpoints):
    public void assertCanUpdateBranch(AuthContext ctx) {
        // similar: rbac.hasAnyRole(ctx.rolesCompany(), allowedUpdateBranchRoles)
        throw new UnsupportedOperationException("Configura cuando lo necesites");
    }

    public void assertCanListBranches(AuthContext ctx) {
        // Por defecto permitimos listar si hay contexto y companyId en el token.
        // Si quieres reglas más estrictas, reemplaza por rbac.hasAnyRole(...) usando roles permitidos.
        if (ctx == null) throw new AuthorizationException("No autorizado (sin contexto)");
        if (ctx.companyId() == null) throw new AuthorizationException("No autorizado para listar sucursales (cid ausente)");
    }
}
