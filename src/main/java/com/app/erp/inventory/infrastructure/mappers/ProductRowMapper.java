package com.app.erp.inventory.infrastructure.mappers;

import com.app.erp.inventory.application.internal.messages.results.CreateProductResult;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Mapea el SELECT final del SP usp_Product_Create → CreateProductResult. */
public class ProductRowMapper implements RowMapper<CreateProductResult> {

    @Override
    public CreateProductResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        CreateProductResult out = new CreateProductResult();

        out.setProductId(rs.getInt("ProductID"));
        out.setSku(rs.getString("SKU"));
        out.setProductName(rs.getString("ProductName"));

        int catId = rs.getInt("CategoryID");
        out.setCategoryId(rs.wasNull() ? null : catId);

        out.setUomId(rs.getInt("UOMID"));

        // Booleans como wrappers (respetar NULL si la columna lo permite)
        boolean b = rs.getBoolean("IsSerialized");
        out.setSerialized(rs.wasNull() ? null : b);

        b = rs.getBoolean("IsBatchControlled");
        out.setBatchControlled(rs.wasNull() ? null : b);

        BigDecimal dec = rs.getBigDecimal("ReorderLevel");
        out.setReorderLevel(dec);

        int i = rs.getInt("LeadTimeDays");
        out.setLeadTimeDays(rs.wasNull() ? null : i);

        out.setWeight(rs.getBigDecimal("Weight"));
        out.setVolume(rs.getBigDecimal("Volume"));

        i = rs.getInt("Status");
        out.setStatus(rs.wasNull() ? null : i);

        Timestamp ts = rs.getTimestamp("CreatedUtc");
        out.setCreatedUtc(ts != null ? ts.toInstant() : null);

        ts = rs.getTimestamp("UpdatedUtc");
        out.setUpdatedUtc(ts != null ? ts.toInstant() : null);

        i = rs.getInt("CompanyID");
        out.setCompanyId(rs.wasNull() ? null : i);

        return out;
    }
}
