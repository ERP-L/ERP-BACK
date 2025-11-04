package com.app.erp.inventory.infrastructure.repository.queries;

import com.app.erp.inventory.application.port.InventoryReadPort;
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
}
