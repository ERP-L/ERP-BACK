package com.app.erp.iam.interfaces.rest.resources;

import com.app.erp.iam.interfaces.rest.resources.dtos.CompanyDto;
import com.app.erp.iam.interfaces.rest.resources.dtos.SecurityUserDto;
import com.app.erp.iam.interfaces.rest.resources.dtos.TenantUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "SignUpRequest", description = "Payload para registro de superadmin + compañía")
public class SignUpRequest {

    @NotNull @Valid
    private CompanyDto company;

    @NotNull @Valid
    private SecurityUserDto securityUser;

    @NotNull @Valid
    private TenantUserDto tenantUser;

    // Getters/Setters
    public CompanyDto getCompany() { return company; }
    public void setCompany(CompanyDto company) { this.company = company; }
    public SecurityUserDto getSecurityUser() { return securityUser; }
    public void setSecurityUser(SecurityUserDto securityUser) { this.securityUser = securityUser; }
    public TenantUserDto getTenantUser() { return tenantUser; }
    public void setTenantUser(TenantUserDto tenantUser) { this.tenantUser = tenantUser; }
}
