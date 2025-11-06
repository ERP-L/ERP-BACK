package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.dtos.results.CreateWarehouseResult;

public interface WarehouseWritePort {
    CreateWarehouseResult createWarehouse(CreateWarehouseCommand command);
}
