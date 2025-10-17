package com.app.erp.inventory.application.internal.port;

import com.app.erp.inventory.application.internal.messages.results.UnitOfMeasureResult;

import java.util.List;

public interface UnitOfMeasureReadPort {
    List<UnitOfMeasureResult> getAll();
}
