package com.app.erp.inventory.infrastructure.repository.queries;

import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.inventory.application.port.InventoryReadPort;
import com.app.erp.shared.security.AuthContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public ProductDetailsResult getProductDetailsInWarehouse(int warehouseId, int productId,
                                                              String orderBatch, int pageBatch, int sizeBatch,
                                                              String orderSerial, int pageSerial, int sizeSerial,
                                                              AuthContext auth) {
        // This gateway only supplies simple query-based reads (e.g., category→company).
        // The SP-backed implementation is provided in InventoryReadRepositorySp.
        throw new UnsupportedOperationException("getProductDetailsInWarehouse is not implemented in InventoryReadGateway. Use the SP-backed repository implementation.");
    }
}
