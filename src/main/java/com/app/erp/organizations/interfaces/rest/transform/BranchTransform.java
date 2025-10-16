// com.app.erp.organizations.interfaces.rest.transform.BranchTransform
package com.app.erp.organizations.interfaces.rest.transform;

import com.app.erp.organizations.application.dtos.commands.RegisterBranchCommand;
import com.app.erp.organizations.application.dtos.results.RegisterBranchResult;
import com.app.erp.organizations.interfaces.rest.resources.BranchResponse;
import com.app.erp.organizations.interfaces.rest.resources.CreateBranchRequest;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Component;

@Component
public class BranchTransform {

    /** Construye el Command usando SIEMPRE el companyId del JWT. */
    public RegisterBranchCommand toCommand(AuthContext ctx, CreateBranchRequest req) {
        return new RegisterBranchCommand(
                ctx.companyId(),
                req.name(),
                req.address(),
                req.ubigeoId()
        );
    }

    /** Mapea el resultado de application a la response pública. */
    public BranchResponse toResponse(RegisterBranchResult result) {
        return new BranchResponse(
                result.branchId(),
                result.companyId(),
                result.name(),
                result.address(),
                result.ubigeoId(),
                result.isActive(),
                result.createdUtc()
        );
    }
}
