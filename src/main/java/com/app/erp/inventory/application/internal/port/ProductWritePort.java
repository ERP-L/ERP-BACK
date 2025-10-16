package com.app.erp.inventory.application.internal.port;

import com.app.erp.inventory.application.internal.messages.commands.CreateProductCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateProductResult;

public interface ProductWritePort {
    CreateProductResult createProduct(CreateProductCommand command);
}
