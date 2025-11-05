package com.app.erp.inventory.infrastructure.mapper;

import com.app.erp.inventory.application.dtos.results.CreateLocationResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class LocationRowMapper implements RowMapper<CreateLocationResult> {

    @Override
    public CreateLocationResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedUtc");
        CreateLocationResult out = new CreateLocationResult();
        out.setLocationId(rs.getInt("LocationID"));
        out.setWarehouseId(rs.getInt("WarehouseID"));
        out.setParentId(rs.getObject("ParentID") == null ? null : rs.getInt("ParentID"));
        out.setCode(rs.getString("Code"));
        out.setAllowStock(rs.getBoolean("AllowStock"));
        out.setCreatedUtc(ts == null ? null : ts.toInstant());
        return out;
    }
}
