package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.commands.CreateProductCommand;
import com.app.erp.inventory.application.dtos.results.CreateProductResult;

public interface ProductWritePort {
    CreateProductResult createProduct(CreateProductCommand command);
}
