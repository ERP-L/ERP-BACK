package com.app.erp.iam.application.dto.commands;

public class RegisterCommand {
    private CompanyCommand company;
    private SecurityUserCommand securityUser;
    private TenantUserCommand tenantUser;

    public RegisterCommand() { }

    public RegisterCommand(CompanyCommand company,
                           SecurityUserCommand securityUser,
                           TenantUserCommand tenantUser) {
        this.company = company;
        this.securityUser = securityUser;
        this.tenantUser = tenantUser;
    }

    public CompanyCommand getCompany() { return company; }
    public SecurityUserCommand getSecurityUser() { return securityUser; }
    public TenantUserCommand getTenantUser() { return tenantUser; }
}
