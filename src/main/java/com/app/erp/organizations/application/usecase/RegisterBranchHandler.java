package com.app.erp.organizations.application.usecase;

import com.app.erp.organizations.application.config.OrganizationsRulesProperties;
import com.app.erp.organizations.application.dtos.commands.RegisterBranchCommand;
import com.app.erp.organizations.application.dtos.results.RegisterBranchResult;
import com.app.erp.shared.exceptions.InvalidInputException;
import com.app.erp.shared.exceptions.ResourceConflictException;
import com.app.erp.organizations.application.port.CatalogsQueryGateway;
import com.app.erp.organizations.application.port.OrganizationsCommandGateway;
import com.app.erp.organizations.application.port.OrganizationsQueryGateway;
import com.app.erp.organizations.application.security.OrganizationsAuthorizationPolicy;
import com.app.erp.organizations.domain.Branch;
import com.app.erp.organizations.domain.valueobjects.AddressLine;
import com.app.erp.organizations.domain.valueobjects.BranchName;
import com.app.erp.organizations.domain.valueobjects.UbigeoId;
import com.app.erp.shared.security.AuthContext;

import java.util.Objects;

public final class RegisterBranchHandler {

    private final OrganizationsAuthorizationPolicy authz;
    private final OrganizationsCommandGateway commandGateway;
    private final OrganizationsQueryGateway queryGateway;
    private final CatalogsQueryGateway catalogsGateway;
    private final OrganizationsRulesProperties rules;

    public RegisterBranchHandler(OrganizationsAuthorizationPolicy authz,
                                 OrganizationsCommandGateway commandGateway,
                                 OrganizationsQueryGateway queryGateway,
                                 CatalogsQueryGateway catalogsGateway,
                                 OrganizationsRulesProperties rules) {
        this.authz = Objects.requireNonNull(authz);
        this.commandGateway = Objects.requireNonNull(commandGateway);
        this.queryGateway = Objects.requireNonNull(queryGateway);
        this.catalogsGateway = Objects.requireNonNull(catalogsGateway);
        this.rules = Objects.requireNonNull(rules);
    }

    public RegisterBranchResult handle(AuthContext auth, RegisterBranchCommand cmd) {
        // 1) Autorización (solo por roles_company hoy)
        authz.assertCanCreateBranch(auth);

        // 2) Validaciones de formato (VOs)
        var nameVO    = new BranchName(cmd.name());
        var addressVO = AddressLine.ofNullable(cmd.address());
        var ubigeoVO  = new UbigeoId(cmd.ubigeoId());

        // 3) Validación referencial: Ubigeo debe existir
        if (!catalogsGateway.existsUbigeo(ubigeoVO.value())) {
            throw new InvalidInputException("Ubigeo no existe: " + ubigeoVO.value());
        }

        // 4) (Opcional según reglas) Compañía activa
        if (rules.isRequireCompanyActive() && !queryGateway.isCompanyActive(cmd.companyId())) {
            throw new InvalidInputException("La compañía no está activa para crear sucursales");
        }

        // 5) (Opcional) Unicidad de nombre por compañía
        if (rules.isEnforceUniqueBranchName() &&
                queryGateway.existsBranchNameInCompany(cmd.companyId(), nameVO.value())) {
            throw new ResourceConflictException("Ya existe una sucursal con ese nombre en la compañía");
        }

        // 6) Crear entidad y persistir (SP)
        Branch newBranch = Branch.register(cmd.companyId(), nameVO, addressVO, ubigeoVO);
        Branch persisted = commandGateway.registerBranch(newBranch);

        // 7) Resultado
        return new RegisterBranchResult(
                persisted.getBranchId(),
                persisted.getCompanyId(),
                persisted.getName().value(),
                persisted.getAddress() != null ? persisted.getAddress().value() : null,
                persisted.getUbigeoId().value(),
                persisted.isActive(),
                persisted.getCreatedUtc()
        );
    }
}
