package com.app.erp.inventory.infrastructure.mapper;

import com.app.erp.inventory.application.dtos.results.ReparentProductCategoryResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class ProductCategoryDbMapper {
    private ProductCategoryDbMapper() {}

    public static ReparentProductCategoryResult toReparentResult(ResultSet rs) throws SQLException {
        int cid = rs.getInt("CategoryID");
        String name = rs.getString("CategoryName");
        Integer parent = rs.getObject("ParentCategoryID") == null ? null : rs.getInt("ParentCategoryID");
        boolean active = rs.getBoolean("IsActive");
        Timestamp createdTs = rs.getTimestamp("CreatedUtc");
        OffsetDateTime created = createdTs == null ? null : createdTs.toInstant().atOffset(ZoneOffset.UTC);
        int compId = rs.getInt("CompanyID");

        var out = new ReparentProductCategoryResult();
        out.setCategoryId(cid);
        out.setCategoryName(name);
        out.setParentCategoryId(parent);
        out.setIsActive(active);
        out.setCreatedUtc(created);
        out.setCompanyId(compId);
        return out;
    }
}
