package com.app.erp.organizations.infrastructure.mapper;

import com.app.erp.organizations.domain.Branch;
import com.app.erp.organizations.domain.valueobjects.AddressLine;
import com.app.erp.organizations.domain.valueobjects.BranchName;
import com.app.erp.organizations.domain.valueobjects.UbigeoId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper de filas JDBC a modelos del BC Organizations.
 */
public final class BranchDbMapper {
    private BranchDbMapper() {}

    public static Branch toDomainBranch(ResultSet rs) throws SQLException {
        int branchId = rs.getInt("BranchID");
        int compId = rs.getInt("CompanyID");
        String name = rs.getString("Name");
        String address = rs.getString("Address");
        String ubigeo = rs.getString("UbigeoID");
        boolean isActive = rs.getBoolean("IsActive");

        Timestamp createdTs = safeGetTimestamp(rs, "CreatedUtc");
        Timestamp updatedTs = safeGetTimestamp(rs, "UpdatedUtc");
        OffsetDateTime created = createdTs != null ? createdTs.toInstant().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime updated = updatedTs != null ? updatedTs.toInstant().atOffset(ZoneOffset.UTC) : null;

        return Branch.fromPersistence(
                branchId,
                compId,
                new BranchName(name),
                address != null ? new AddressLine(address) : null,
                new UbigeoId(ubigeo),
                isActive,
                created,
                updated
        );
    }

    private static Timestamp safeGetTimestamp(ResultSet rs, String column) throws SQLException {
        try {
            return rs.getTimestamp(column);
        } catch (SQLException e) {
            // Por si el result set correspondiente no devuelve estas columnas en algún SP
            return null;
        }
    }
}
