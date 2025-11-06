package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.results.LocationResult;
import com.app.erp.inventory.application.port.LocationReadPort;
import com.app.erp.inventory.infrastructure.mapper.LocationListRowMapper;
import com.app.erp.shared.exceptions.ApplicationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class LocationReadGateway implements LocationReadPort {

    private final DataSource ds;

    public LocationReadGateway(DataSource ds) { this.ds = Objects.requireNonNull(ds); }

    @Override
    public List<LocationResult> listLocations(int warehouseId, Boolean onlyAllowStock) {
        final String call = "{ call inventory.usp_Location_List(?,?) }";
        try (Connection con = ds.getConnection(); CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, warehouseId);
            if (onlyAllowStock == null) cs.setNull(2, Types.BIT); else cs.setBoolean(2, onlyAllowStock);

            boolean has = cs.execute();
            List<LocationResult> out = new ArrayList<>();
            if (has) try (ResultSet rs = cs.getResultSet()) {
                while (rs.next()) {
                    LocationResult r = new LocationResult();
                    r.setLocationId(rs.getInt("LocationID"));
                    r.setWarehouseId(rs.getInt("WarehouseID"));
                    r.setParentId(rs.getObject("ParentID") == null ? null : rs.getInt("ParentID"));
                    r.setParentCode(rs.getString("ParentCode"));
                    r.setCode(rs.getString("Code"));
                    r.setAllowStock(rs.getBoolean("AllowStock"));
                    Timestamp created = rs.getTimestamp("CreatedUtc");
                    r.setCreatedUtc(created == null ? null : created.toInstant());
                    out.add(r);
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new ApplicationException("Error leyendo locations", ex);
        }
    }
}
