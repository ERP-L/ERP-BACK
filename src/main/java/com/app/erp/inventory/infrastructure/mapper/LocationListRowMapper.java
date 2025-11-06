package com.app.erp.inventory.infrastructure.mapper;

import com.app.erp.inventory.application.dtos.results.LocationResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LocationListRowMapper implements RowMapper<LocationResult> {

    @Override
    public LocationResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp ts = rs.getTimestamp("CreatedUtc");
        LocationResult out = new LocationResult();
        out.setLocationId(rs.getInt("LocationID"));
        out.setWarehouseId(rs.getInt("WarehouseID"));
        out.setParentId(rs.getObject("ParentID") == null ? null : rs.getInt("ParentID"));
        out.setParentCode(rs.getString("ParentCode"));
        out.setCode(rs.getString("Code"));
        out.setAllowStock(rs.getBoolean("AllowStock"));
        out.setCreatedUtc(ts == null ? null : ts.toInstant());
        return out;
    }
}
