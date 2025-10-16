package com.app.erp.organizations.application.usecase;

import com.app.erp.organizations.application.port.OrganizationsQueryGateway;
import com.app.erp.organizations.application.security.OrganizationsAuthorizationPolicy;
import com.app.erp.organizations.domain.Branch;
import com.app.erp.shared.security.AuthContext;

import java.util.List;
import java.util.Objects;

public final class ListBranchesHandler {

    private final OrganizationsAuthorizationPolicy authz;
    private final OrganizationsQueryGateway queryGateway;

    public ListBranchesHandler(OrganizationsAuthorizationPolicy authz, OrganizationsQueryGateway queryGateway) {
        this.authz = Objects.requireNonNull(authz);
        this.queryGateway = Objects.requireNonNull(queryGateway);
    }

    public List<Branch> handle(AuthContext auth, Boolean onlyActive) {
        if (auth == null) throw new com.app.erp.shared.exceptions.AuthorizationException("No autorizado (sin contexto)");
        if (auth.companyId() == null) throw new com.app.erp.shared.exceptions.AuthorizationException("Tu token no tiene compañía asociada (cid).");

        authz.assertCanListBranches(auth);

        return queryGateway.listBranchesByCompany(auth.companyId(), onlyActive);
    }
}
