package com.app.erp.inventory.application.internal.port;

import com.app.erp.inventory.application.internal.messages.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;

public interface WarehouseWritePort {
    CreateWarehouseResult createWarehouse(CreateWarehouseCommand command);
}
