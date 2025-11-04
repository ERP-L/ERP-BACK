package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.shared.security.AuthContext;

public interface InventoryWritePort {
    PostInventoryMovementResult postMovement(PostInventoryMovementCommand command, AuthContext authContext);
}
