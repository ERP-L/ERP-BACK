package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.commands.ReparentProductCategoryCommand;
import com.app.erp.inventory.application.dtos.results.ReparentProductCategoryResult;
import com.app.erp.inventory.application.port.ProductCategoryWritePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.exceptions.AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ReparentProductCategoryHandler {

    private final ProductCategoryWritePort writePort;

    public ReparentProductCategoryHandler(ProductCategoryWritePort writePort) {
        this.writePort = Objects.requireNonNull(writePort);
    }

    @Transactional
    public ReparentProductCategoryResult handle(ReparentProductCategoryCommand cmd, AuthContext auth) {
        if (auth == null || auth.companyId() == null) throw new AuthorizationException("Token sin companyId (cid)");

        return writePort.reparentProductCategory(auth.companyId(), cmd.getCategoryId(), cmd.getNewParentCategoryId());
    }
}
