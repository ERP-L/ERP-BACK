package com.app.erp.inventory.interfaces.rest.resources;

import com.app.erp.inventory.application.usecase.ListUnitOfMeasuresHandler;
import com.app.erp.inventory.interfaces.rest.contracts.UnitOfMeasureResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/inventory/uoms")
public class UnitOfMeasureController {

    private final ListUnitOfMeasuresHandler listService;

    public UnitOfMeasureController(ListUnitOfMeasuresHandler listService) {
        this.listService = listService;
    }

    @GetMapping
    public ResponseEntity<List<UnitOfMeasureResponse>> list() {
        var rows = listService.handle();
        return ResponseEntity.ok(rows);
    }
}
