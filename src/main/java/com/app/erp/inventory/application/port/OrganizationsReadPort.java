package com.app.erp.inventory.application.port;

public interface OrganizationsReadPort {
    /**
     * @return CompanyID para la Branch dada, o null si la branch no existe.
     */
    Integer getBranchCompanyId(int branchId);
}