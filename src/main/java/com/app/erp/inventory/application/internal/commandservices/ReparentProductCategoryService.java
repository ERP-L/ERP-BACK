package com.app.erp.inventory.application.internal.commandservices;

import com.app.erp.inventory.application.internal.messages.commands.ReparentProductCategoryCommand;
import com.app.erp.inventory.application.internal.messages.results.ReparentProductCategoryResult;
import com.app.erp.inventory.application.internal.port.ProductCategoryWritePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.exceptions.AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ReparentProductCategoryService {

    private final ProductCategoryWritePort writePort;

    public ReparentProductCategoryService(ProductCategoryWritePort writePort) {
        this.writePort = Objects.requireNonNull(writePort);
    }

    @Transactional
    public ReparentProductCategoryResult handle(ReparentProductCategoryCommand cmd, AuthContext auth) {
        if (auth == null || auth.companyId() == null) throw new AuthorizationException("Token sin companyId (cid)");

        return writePort.reparentProductCategory(auth.companyId(), cmd.getCategoryId(), cmd.getNewParentCategoryId());
    }
}
