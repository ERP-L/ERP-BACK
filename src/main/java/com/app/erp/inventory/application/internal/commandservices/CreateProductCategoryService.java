package com.app.erp.inventory.application.internal.commandservices;

import com.app.erp.inventory.application.internal.messages.commands.CreateProductCategoryCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateProductCategoryResult;
import com.app.erp.inventory.application.internal.port.ProductCategoryWritePort;
import com.app.erp.inventory.application.internal.security.InventoryAuthorizationPolicy;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.exceptions.AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateProductCategoryService {

    private final ProductCategoryWritePort writePort;
    private final InventoryAuthorizationPolicy policy;

    public CreateProductCategoryService(ProductCategoryWritePort writePort, InventoryAuthorizationPolicy policy) {
        this.writePort = Objects.requireNonNull(writePort);
        this.policy = Objects.requireNonNull(policy);
    }

    @Transactional
    public CreateProductCategoryResult handle(CreateProductCategoryCommand cmd, AuthContext auth) {
        if (auth == null || auth.companyId() == null) throw new AuthorizationException("Token sin companyId");

        // Authorization + ownership validation (policy should check parentCategory if provided)
        policy.checkCreateCategory(cmd, auth);

        Integer id = writePort.createProductCategory(
                auth.companyId(),
                cmd.getCategoryName(),
                cmd.getDescription(),
                cmd.getParentCategoryId(),
                cmd.getIsActive() == null ? true : cmd.getIsActive()
        );

        return new CreateProductCategoryResult(id);
    }
}
