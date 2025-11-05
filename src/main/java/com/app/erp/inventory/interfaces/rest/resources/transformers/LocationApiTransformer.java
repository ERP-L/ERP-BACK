package com.app.erp.inventory.interfaces.rest.resources.transformers;

import com.app.erp.inventory.application.dtos.commands.CreateLocationCommand;
import com.app.erp.inventory.application.dtos.results.CreateLocationResult;
import com.app.erp.inventory.application.dtos.results.LocationResult;
import com.app.erp.inventory.interfaces.rest.contracts.CreateLocationRequest;
import com.app.erp.inventory.interfaces.rest.contracts.LocationResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LocationApiTransformer {

    public CreateLocationCommand toCommand(CreateLocationRequest req) {
        CreateLocationCommand cmd = new CreateLocationCommand();
        cmd.setCode(nullIfBlank(req.getCode()));
        cmd.setParentId(req.getParentId());
        cmd.setAllowStock(req.getAllowStock());
        return cmd;
    }

    public LocationResponse toResponse(CreateLocationResult res) {
        LocationResponse dto = new LocationResponse();
        dto.setLocationId(res.getLocationId());
        dto.setWarehouseId(res.getWarehouseId());
        dto.setParentId(res.getParentId());
        dto.setCode(res.getCode());
        dto.setAllowStock(res.getAllowStock());
        dto.setCreatedUtc(res.getCreatedUtc() == null ? null : res.getCreatedUtc().toString());
        return dto;
    }

    public LocationResponse toResponse(LocationResult res) {
        LocationResponse dto = new LocationResponse();
        dto.setLocationId(res.getLocationId());
        dto.setWarehouseId(res.getWarehouseId());
        dto.setParentId(res.getParentId());
        dto.setParentCode(res.getParentCode());
        dto.setCode(res.getCode());
        dto.setAllowStock(res.getAllowStock());
        dto.setCreatedUtc(res.getCreatedUtc() == null ? null : res.getCreatedUtc().toString());
        return dto;
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
