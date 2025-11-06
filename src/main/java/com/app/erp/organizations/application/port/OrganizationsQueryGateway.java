package com.app.erp.organizations.application.port;

/** Consultas de apoyo a reglas/validaciones del caso de uso. */
public interface OrganizationsQueryGateway {
    /** ¿Existe ya un nombre de sucursal dentro de la misma compañía? */
    boolean existsBranchNameInCompany(int companyId, String branchName);

    /** ¿La compañía está activa para operar? */
    boolean isCompanyActive(int companyId);

    /** Lista branches de una compañía. onlyActive: null = no filtra; true = solo activas; false = solo inactivas */
    java.util.List<com.app.erp.organizations.domain.Branch> listBranchesByCompany(int companyId, Boolean onlyActive);
}
