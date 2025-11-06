package com.app.erp.inventory.application.internal.security;

import com.app.erp.inventory.application.dtos.commands.CreateProductCommand;
import com.app.erp.inventory.application.dtos.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.port.InventoryReadPort;
import com.app.erp.inventory.application.port.OrganizationsReadPort;
import com.app.erp.shared.exceptions.AuthorizationException;
import com.app.erp.shared.exceptions.NotFoundException;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.RbacService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InventoryAuthorizationPolicy {

    private final RbacService rbac;
    private final OrganizationsReadPort orgReadPort;
    private final InventoryReadPort inventoryReadPort;

    // puedes inyectar por properties; default "1"
    private final Set<Integer> allowedRolesCreateWarehouse;
    private final Set<Integer> allowedRolesCreateProduct;

    public InventoryAuthorizationPolicy(
            RbacService rbac,
            OrganizationsReadPort orgReadPort,
            InventoryReadPort inventoryReadPort,
            @Value("${inventory.authz.allowed-roles.create-warehouse:1}") String rolesWhCsv,
            @Value("${inventory.authz.allowed-roles.create-product:1}") String rolesProdCsv) {
        this.rbac = rbac;
        this.orgReadPort = orgReadPort;
        this.inventoryReadPort = inventoryReadPort;
        this.allowedRolesCreateWarehouse = parseCsvToIntSet(rolesWhCsv);
        this.allowedRolesCreateProduct  = parseCsvToIntSet(rolesProdCsv);
    }

    public void checkCreateWarehouse(CreateWarehouseCommand cmd, AuthContext auth) {
        // RBAC (usa tu método hasAnyRole)
        if (!rbac.hasAnyRole(auth.rolesCompany(), allowedRolesCreateWarehouse)) {
            throw new AuthorizationException("No tienes permisos para crear warehouses.");
        }

        // Ownership / tenancy
        Integer branchCompanyId = orgReadPort.getBranchCompanyId(cmd.getBranchId());
        if (branchCompanyId == null) {
            throw new NotFoundException("La sucursal (branch) no existe.");
        }
        if (!branchCompanyId.equals(auth.companyId())) { // record accessor
            throw new AuthorizationException("La sucursal no pertenece a tu compañía.");
        }
    }

    public void checkCreateProduct(CreateProductCommand cmd, AuthContext auth) {
        // Debe existir compañía en el token
        if (auth.companyId() == null) {
            throw new AuthorizationException("El token no tiene compañía asociada.");
        }
        // RBAC de compañía para crear producto
        if (!rbac.hasAnyRole(auth.rolesCompany(), allowedRolesCreateProduct)) {
            throw new AuthorizationException("No tienes permisos para crear productos.");
        }
        // Si se envía categoryId, validar que pertenezca a la misma compañía
        if (cmd.getCategoryId() != null) {
            Integer catCompany = inventoryReadPort.getCategoryCompanyId(cmd.getCategoryId());
            if (catCompany == null) {
                throw new NotFoundException("CategoryID no existe.");
            }
            if (!catCompany.equals(auth.companyId())) {
                throw new AuthorizationException("CategoryID no pertenece a tu compañía.");
            }
        }
    }

    public void checkCreateCategory(com.app.erp.inventory.application.dtos.commands.CreateProductCategoryCommand cmd, AuthContext auth) {
        // Debe existir compañía en el token
        if (auth.companyId() == null) throw new com.app.erp.shared.exceptions.AuthorizationException("El token no tiene compañía asociada.");

        // RBAC: reuse create-product roles for now (or configure new property)
        if (!rbac.hasAnyRole(auth.rolesCompany(), allowedRolesCreateProduct)) {
            throw new com.app.erp.shared.exceptions.AuthorizationException("No tienes permisos para crear categorías.");
        }

        // Si viene parentCategoryId, validar pertenencia a la compañía
        if (cmd.getParentCategoryId() != null) {
            Integer pc = inventoryReadPort.getCategoryCompanyId(cmd.getParentCategoryId());
            if (pc == null) throw new com.app.erp.shared.exceptions.NotFoundException("ParentCategoryID no existe.");
            if (!pc.equals(auth.companyId())) throw new com.app.erp.shared.exceptions.AuthorizationException("ParentCategoryID no pertenece a tu compañía.");
        }
    }

    private static Set<Integer> parseCsvToIntSet(String csv) {
        return Stream.of(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
    }
}
