package com.app.erp.organizations.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "organizations.rules")
public class OrganizationsRulesProperties {

    /** Verificar unicidad de nombre de branch por company (false por ahora). */
    private boolean enforceUniqueBranchName = false;

    /** Exigir que la compañía esté activa para crear (false por ahora). */
    private boolean requireCompanyActive = false;

    public boolean isEnforceUniqueBranchName() { return enforceUniqueBranchName; }
    public void setEnforceUniqueBranchName(boolean enforceUniqueBranchName) { this.enforceUniqueBranchName = enforceUniqueBranchName; }

    public boolean isRequireCompanyActive() { return requireCompanyActive; }
    public void setRequireCompanyActive(boolean requireCompanyActive) { this.requireCompanyActive = requireCompanyActive; }
}
