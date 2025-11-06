package com.app.erp.inventory.interfaces.rest.resources.transformers;

import com.app.erp.inventory.application.dtos.commands.CreateProductCommand;
import com.app.erp.inventory.application.dtos.results.CreateProductResult;
import com.app.erp.inventory.interfaces.rest.contracts.CreateProductRequest;
import com.app.erp.inventory.interfaces.rest.contracts.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductApiTransformer {

    public CreateProductCommand toCommand(CreateProductRequest req) {
        CreateProductCommand cmd = new CreateProductCommand();
        cmd.setSku(nullIfBlank(req.getSku()));
        cmd.setProductName(req.getProductName());
        cmd.setCategoryId(req.getCategoryId());
        cmd.setUomId(req.getUomId());
        cmd.setIsSerialized(req.getIsSerialized());
        cmd.setIsBatchControlled(req.getIsBatchControlled());
        cmd.setReorderLevel(req.getReorderLevel());
        cmd.setLeadTimeDays(req.getLeadTimeDays());
        cmd.setWeight(req.getWeight());
        cmd.setVolume(req.getVolume());

        // NO seteamos companyId/status/createdUtc aquí; los impone el Service.
        return cmd;
    }

    public ProductResponse toResponse(CreateProductResult res) {
        ProductResponse dto = new ProductResponse();
        dto.setProductId(res.getProductId());
        dto.setSku(res.getSku());
        dto.setProductName(res.getProductName());
        dto.setCategoryId(res.getCategoryId());
        dto.setUomId(res.getUomId());
        dto.setIsSerialized(res.getIsSerialized());
        dto.setIsBatchControlled(res.getIsBatchControlled());
        dto.setReorderLevel(res.getReorderLevel());
        dto.setLeadTimeDays(res.getLeadTimeDays());
        dto.setWeight(res.getWeight());
        dto.setVolume(res.getVolume());
        dto.setStatus(res.getStatus());
        dto.setCreatedUtc(res.getCreatedUtc() == null ? null : res.getCreatedUtc().toString());
        dto.setUpdatedUtc(res.getUpdatedUtc() == null ? null : res.getUpdatedUtc().toString());
        dto.setCompanyId(res.getCompanyId());
        return dto;
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
