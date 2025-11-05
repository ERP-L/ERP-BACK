package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.results.RecentMovementResult;
import com.app.erp.inventory.application.port.InventoryReadPort;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetRecentMovementsHandler {

    private final InventoryReadPort readPort;

    public GetRecentMovementsHandler(InventoryReadPort readPort) {
        this.readPort = readPort;
    }

    public List<RecentMovementResult> handle(AuthContext auth,
                                             Integer warehouseId,
                                             String search,
                                             LocalDate dateFrom,
                                             LocalDate dateTo,
                                             String type,
                                             int page,
                                             int size) {
        return readPort.getRecentMovements(warehouseId, search, dateFrom, dateTo, type, page, size, auth);
    }
}
