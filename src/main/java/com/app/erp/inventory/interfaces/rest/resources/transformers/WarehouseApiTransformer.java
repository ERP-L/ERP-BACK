package com.app.erp.inventory.interfaces.rest.resources.transformers;

import com.app.erp.inventory.application.internal.messages.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;
import com.app.erp.inventory.interfaces.rest.contracts.CreateWarehouseRequest;
import com.app.erp.inventory.interfaces.rest.contracts.WarehouseResponse;
import com.app.erp.shared.exceptions.InvalidInputException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WarehouseApiTransformer {

    public CreateWarehouseCommand toCommand(CreateWarehouseRequest req) {
        CreateWarehouseCommand cmd = new CreateWarehouseCommand();
        cmd.setBranchId(req.getBranchId());
        cmd.setWarehouseCode(nullIfBlank(req.getWarehouseCode()));
        cmd.setWarehouseName(req.getWarehouseName());
        cmd.setAddress(nullIfBlank(req.getAddress()));
        cmd.setPhone(nullIfBlank(req.getPhone()));
        cmd.setContact(nullIfBlank(req.getContact()));

        return cmd;
    }

    public WarehouseResponse toResponse(CreateWarehouseResult res) {
        WarehouseResponse dto = new WarehouseResponse();
        dto.setWarehouseId(res.getWarehouseId());
        dto.setWarehouseCode(res.getWarehouseCode());
        dto.setWarehouseName(res.getWarehouseName());
        dto.setAddress(res.getAddress());
        dto.setPhone(res.getPhone());
        dto.setContact(res.getContact());
        dto.setIsActive(res.getActive());
        dto.setCreatedUtc(res.getCreatedUtc() == null ? null : res.getCreatedUtc().toString());
        dto.setBranchId(res.getBranchId());
        return dto;
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
