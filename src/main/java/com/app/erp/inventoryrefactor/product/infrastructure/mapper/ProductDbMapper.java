package com.app.erp.inventoryrefactor.product.infrastructure.mapper;

import com.app.erp.inventoryrefactor.product.domain.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;

public final class ProductDbMapper {

    private ProductDbMapper() {}

    public static Product fromResultSet(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("CreatedUtc");
        Timestamp updated = rs.getTimestamp("UpdatedUtc");

        return new Product(
                rs.getInt("ProductID"),
                rs.getString("SKU"),
                rs.getString("ProductName"),
                rs.getObject("CategoryID") == null ? null : rs.getInt("CategoryID"),
                rs.getObject("UOMID") == null ? null : rs.getInt("UOMID"),
                rs.getBoolean("IsSerialized"),
                rs.getBoolean("IsBatchControlled"),
                rs.getObject("ReorderLevel") == null ? null : rs.getBigDecimal("ReorderLevel"),
                rs.getObject("LeadTimeDays") == null ? null : rs.getInt("LeadTimeDays"),
                rs.getObject("Weight") == null ? null : rs.getBigDecimal("Weight"),
                rs.getObject("Volume") == null ? null : rs.getBigDecimal("Volume"),
                rs.getString("Status"),
                created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC),
                updated == null ? null : updated.toInstant().atOffset(ZoneOffset.UTC),
                rs.getInt("CompanyID")
        );
    }
}
