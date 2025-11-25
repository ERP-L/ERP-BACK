package com.app.erp.finances.costcenter.infrastructure.mapper;

import com.app.erp.finances.costcenter.domain.CostCenter;
import com.app.erp.finances.costcenter.interfaces.rest.responses.CostCenterResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;

public final class CostCenterDbMapper {
    private CostCenterDbMapper() {}

    public static CostCenter fromResultSet(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("DateCreated");
        Timestamp modified = rs.getTimestamp("DateModified");

        Integer userCreated = null;
        Integer userModified = null;
        try { Object uc = rs.getObject("UserCreated"); if (uc != null) userCreated = rs.getInt("UserCreated"); } catch (SQLException ignored) {}
        try { Object um = rs.getObject("UserModified"); if (um != null) userModified = rs.getInt("UserModified"); } catch (SQLException ignored) {}

        return new CostCenter(
                rs.getInt("CostCenterID"),
                rs.getString("Code"),
                rs.getString("Name"),
                rs.getString("Description"),
                rs.getBoolean("IsActive"),
                created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC),
                userCreated,
                modified == null ? null : modified.toInstant().atOffset(ZoneOffset.UTC),
                userModified,
                rs.getInt("AreaID"),
                rs.getInt("CompanyID")
        );
    }

    // nuevo: convierte domain -> response (método estático)
    public static CostCenterResponse toResponse(CostCenter cc) {
        if (cc == null) return null;
        return new CostCenterResponse(
            cc.costCenterId(),
            cc.code(),
            cc.name(),
            cc.description(),
            cc.isActive(),
            cc.dateCreated(),
            cc.userCreated(),
            cc.dateModified(),
            cc.userModified(),
            cc.areaId(),
            cc.companyId()
        );
    }
}
