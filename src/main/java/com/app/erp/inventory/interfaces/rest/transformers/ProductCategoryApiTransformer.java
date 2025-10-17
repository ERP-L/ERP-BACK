package com.app.erp.inventory.interfaces.rest.transformers;

import com.app.erp.inventory.application.internal.messages.commands.CreateProductCategoryCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateProductCategoryResult;
import com.app.erp.inventory.interfaces.rest.contracts.CreateProductCategoryRequest;
import com.app.erp.inventory.interfaces.rest.contracts.ProductCategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryApiTransformer {

    public CreateProductCategoryCommand toCommand(CreateProductCategoryRequest req) {
        CreateProductCategoryCommand cmd = new CreateProductCategoryCommand();
        cmd.setCategoryName(req.getCategoryName());
        cmd.setDescription(req.getDescription());
        cmd.setParentCategoryId(req.getParentCategoryId());
        cmd.setIsActive(req.getIsActive() == null ? Boolean.TRUE : req.getIsActive());
        return cmd;
    }

    public ProductCategoryResponse toResponse(CreateProductCategoryResult res) {
        return new ProductCategoryResponse(res.getCategoryId());
    }
}
