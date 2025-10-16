package com.app.erp.organizations.application.port;

/** Consultas de apoyo a reglas/validaciones del caso de uso. */
public interface OrganizationsQueryGateway {
    /** ¿Existe ya un nombre de sucursal dentro de la misma compañía? */
    boolean existsBranchNameInCompany(int companyId, String branchName);

    /** ¿La compañía está activa para operar? */
    boolean isCompanyActive(int companyId);
}
