package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.UnitOfMeasureResult;

import java.util.List;

public interface UnitOfMeasureReadPort {
    List<UnitOfMeasureResult> getAll();
}
