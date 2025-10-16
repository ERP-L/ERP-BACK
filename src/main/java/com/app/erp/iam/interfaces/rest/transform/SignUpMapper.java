package com.app.erp.iam.interfaces.rest.transform;

import com.app.erp.iam.application.dto.commands.CompanyCommand;
import com.app.erp.iam.application.dto.commands.RegisterCommand;
import com.app.erp.iam.application.dto.commands.SecurityUserCommand;
import com.app.erp.iam.application.dto.commands.TenantUserCommand;
import com.app.erp.iam.interfaces.rest.resources.dtos.CompanyDto;
import com.app.erp.iam.interfaces.rest.resources.dtos.SecurityUserDto;
import com.app.erp.iam.interfaces.rest.resources.SignUpRequest;
import com.app.erp.iam.interfaces.rest.resources.dtos.TenantUserDto;
import org.springframework.stereotype.Component;

@Component
public class SignUpMapper {

    public RegisterCommand toCommand(SignUpRequest resource) {
        CompanyCommand company = toCompanyCommand(resource.getCompany());
        SecurityUserCommand securityUser = toSecurityUserCommand(resource.getSecurityUser());
        TenantUserCommand tenantUser = toTenantUserCommand(resource.getTenantUser());
        return new RegisterCommand(company, securityUser, tenantUser);
    }

    private CompanyCommand toCompanyCommand(CompanyDto dto) {
        if (dto == null) return null;
        return new CompanyCommand(
                dto.getLegalName(),
                dto.getDocumentTypeId(),
                dto.getDocumentNumber(),
                dto.getTradeName(),
                dto.getAddress(),
                dto.getUbigeoId(),
                dto.getPhone(),
                dto.getEmail()
        );
    }

    private SecurityUserCommand toSecurityUserCommand(SecurityUserDto dto) {
        if (dto == null) return null;
        return new SecurityUserCommand(
                dto.getEmail(),
                dto.getUsername(),
                dto.getPassword()
        );
    }

    private TenantUserCommand toTenantUserCommand(TenantUserDto dto) {
        if (dto == null) return null;
        return new TenantUserCommand(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getGender(),
                dto.getPhone(),
                dto.getDocumentTypeId(),
                dto.getDocumentNumber()
        );
    }
}
