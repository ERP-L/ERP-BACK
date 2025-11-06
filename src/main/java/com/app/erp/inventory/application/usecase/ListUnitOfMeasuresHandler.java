package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.results.UnitOfMeasureResult;
import com.app.erp.inventory.application.port.UnitOfMeasureReadPort;
import com.app.erp.inventory.interfaces.rest.contracts.UnitOfMeasureResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ListUnitOfMeasuresHandler {

    private final UnitOfMeasureReadPort readPort;

    public ListUnitOfMeasuresHandler(UnitOfMeasureReadPort readPort) {
        this.readPort = Objects.requireNonNull(readPort);
    }

    public List<UnitOfMeasureResponse> handle() {
        List<UnitOfMeasureResult> rows = readPort.getAll();
        return rows.stream().map(r -> {
            UnitOfMeasureResponse res = new UnitOfMeasureResponse();
            res.setUomId(r.getUomId());
            res.setUomCode(r.getUomCode());
            res.setUomName(r.getUomName());
            res.setDecimalPlaces(r.getDecimalPlaces());
            res.setCreatedUtc(r.getCreatedUtc());
            return res;
        }).collect(Collectors.toList());
    }
}
