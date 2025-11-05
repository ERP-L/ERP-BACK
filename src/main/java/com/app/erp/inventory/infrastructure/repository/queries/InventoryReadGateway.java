package com.app.erp.inventory.infrastructure.repository.queries;

import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.inventory.application.dtos.results.RecentMovementResult;
import com.app.erp.inventory.application.port.InventoryReadPort;
import com.app.erp.shared.security.AuthContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/** Lecturas del BC Inventory para validaciones previas (p. ej., categoría→compañía). */
@Repository
public class InventoryReadGateway implements InventoryReadPort {

    private final JdbcTemplate jdbc;

    public InventoryReadGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Integer getCategoryCompanyId(int categoryId) {
        return jdbc.query(
                "SELECT CompanyID FROM inventory.ProductCategory WHERE CategoryID = ?",
                ps -> ps.setInt(1, categoryId),
                rs -> rs.next() ? rs.getInt("CompanyID") : null
        );
    }

    @Override
    public List<RecentMovementResult> getRecentMovements(Integer warehouseId,
                                                          String search,
                                                          LocalDate dateFrom,
                                                          LocalDate dateTo,
                                                          String type,
                                                          int page,
                                                          int size,
                                                          AuthContext auth) {
        // This gateway provides only simple query-based reads. The SP-backed implementation
        // (InventoryReadRepositorySp) implements recent movements. Throw explicitly so
        // callers don't accidentally use this lightweight gateway for SP-backed reads.
        throw new UnsupportedOperationException("getRecentMovements is not implemented in InventoryReadGateway. Use SP-backed repository implementation.");
    }

    @Override
    public ProductDetailsResult getProductDetailsInWarehouse(int warehouseId, int productId,
                                                              String orderBatch, int pageBatch, int sizeBatch,
                                                              String orderSerial, int pageSerial, int sizeSerial,
                                                              AuthContext auth) {
        // This gateway only supplies simple query-based reads (e.g., category→company).
        // The SP-backed implementation is provided in InventoryReadRepositorySp.
        throw new UnsupportedOperationException("getProductDetailsInWarehouse is not implemented in InventoryReadGateway. Use the SP-backed repository implementation.");
    }
}
