package com.app.erp.organizations.application.port;

import com.app.erp.organizations.domain.Branch;

/** Comandos que modifican estado (INSERT/UPDATE) vía SP. */
public interface OrganizationsCommandGateway {
    /** Persiste una nueva Branch (SP) y devuelve la entidad con ID/fechas llenas. */
    Branch registerBranch(Branch newBranch);
}
