package com.app.erp.inventory.infrastructure.mapper;

import com.app.erp.inventory.application.dtos.results.CreateWarehouseResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class WarehouseRowMapper implements RowMapper<CreateWarehouseResult> {

    @Override
    public CreateWarehouseResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedUtc");

        CreateWarehouseResult out = new CreateWarehouseResult();
        out.setWarehouseId(rs.getInt("WarehouseID"));
        out.setWarehouseCode(rs.getString("WarehouseCode"));
        out.setWarehouseName(rs.getString("WarehouseName"));
        out.setAddress(rs.getString("Address"));
        out.setPhone(rs.getString("Phone"));
        out.setContact(rs.getString("Contact"));
        out.setActive(rs.getBoolean("IsActive"));
        out.setCreatedUtc(ts != null ? ts.toInstant() : null);
        out.setBranchId(rs.getInt("BranchID"));
        return out;
    }
}
