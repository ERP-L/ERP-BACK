package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.commands.CreateLocationCommand;
import com.app.erp.inventory.application.dtos.results.CreateLocationResult;

public interface LocationWritePort {
    CreateLocationResult createLocation(CreateLocationCommand command);
}
