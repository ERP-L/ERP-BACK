package com.app.erp.finances.area.infrastructure.mapper;

import com.app.erp.finances.area.domain.Area;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;

public final class AreaDbMapper {
    private AreaDbMapper() {}

    public static Area fromResultSet(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("CreatedUtc");
        Timestamp updated = rs.getTimestamp("UpdatedUtc");

        Integer userInCharge = null;
        try {
            Object obj = rs.getObject("UserInChargeID");
            if (obj != null) userInCharge = rs.getInt("UserInChargeID");
        } catch (SQLException ex) {
            // column might be absent in some queries; ignore if missing
        }

        return new Area(
                rs.getInt("AreaID"),
                rs.getInt("CompanyID"),
                rs.getInt("BranchID"),
                rs.getString("Name"),
                rs.getString("Code"),
                rs.getString("Description"),
                userInCharge,
                created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC),
                updated == null ? null : updated.toInstant().atOffset(ZoneOffset.UTC)
        );
    }
}
