package com.app.erp.finances.area.infrastructure.repository.sp;

import com.app.erp.finances.area.domain.Area;
import com.app.erp.finances.area.infrastructure.mapper.AreaDbMapper;
import com.app.erp.finances.area.infrastructure.repository.AreaRepositoryPort;
import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.shared.infrastructure.error.DbErrorTranslator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class AreaRepositorySp implements AreaRepositoryPort {

    private final DataSource dataSource;

    public AreaRepositorySp(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    private static class SpGetById {
        static final String CALL = "{ call [core].[usp_Area_GetById](?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_AREA_ID = 2;
    }

    private static class SpGetByBranch {
        static final String CALL = "{ call [core].[usp_Area_GetByBranch](?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_BRANCH_ID = 2;
        static final int IDX_SEARCH = 3;
        static final int IDX_PAGE = 4;
        static final int IDX_PAGE_SIZE = 5;
    }

    private static class SpCreate {
        static final String CALL = "{ call [core].[usp_Area_Create](?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_BRANCH_ID = 2;
        static final int IDX_NAME = 3;
        static final int IDX_CODE = 4;
        static final int IDX_DESCRIPTION = 5;
        static final int IDX_USER_IN_CHARGE = 6;
    }

    @Override
    public Area findById(int companyId, int areaId) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpGetById.CALL)) {
            cs.setInt(SpGetById.IDX_COMPANY_ID, companyId);
            cs.setInt(SpGetById.IDX_AREA_ID, areaId);
            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            // advance if update counts appear
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }
            if (rs != null && rs.next()) {
                return AreaDbMapper.fromResultSet(rs);
            }
            return null;
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public PageResult<Area> findByBranch(int companyId, int branchId, String search, int page, int pageSize) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpGetByBranch.CALL)) {
            cs.setInt(SpGetByBranch.IDX_COMPANY_ID, companyId);
            cs.setInt(SpGetByBranch.IDX_BRANCH_ID, branchId);
            cs.setString(SpGetByBranch.IDX_SEARCH, search);
            cs.setInt(SpGetByBranch.IDX_PAGE, page);
            cs.setInt(SpGetByBranch.IDX_PAGE_SIZE, pageSize);

            cs.execute();
            List<Area> items = new ArrayList<>();
            long total = 0;

            ResultSet rs = cs.getResultSet();
            while (rs == null) {
                int update = cs.getUpdateCount();
                if (update == -1) break;
                if (!cs.getMoreResults()) break;
                rs = cs.getResultSet();
            }

            if (rs == null) return new PageResult<>(0, items);

            ResultSetMetaData meta = rs.getMetaData();
            boolean singleColumn = meta.getColumnCount() == 1;
            String firstLabel = meta.getColumnLabel(1);

            if (singleColumn && firstLabel != null && firstLabel.equalsIgnoreCase("TotalCount")) {
                if (rs.next()) total = rs.getLong(1);
                if (cs.getMoreResults()) rs = cs.getResultSet(); else rs = null;
                if (rs != null) {
                    while (rs.next()) items.add(AreaDbMapper.fromResultSet(rs));
                }
                if (total == 0) total = items.size();
            } else {
                while (rs.next()) items.add(AreaDbMapper.fromResultSet(rs));
                total = items.size();
            }

            return new PageResult<>(total, items);
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public int create(int companyId, Area area) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpCreate.CALL)) {
            cs.setInt(SpCreate.IDX_COMPANY_ID, companyId);
            cs.setInt(SpCreate.IDX_BRANCH_ID, area.branchId());
            cs.setString(SpCreate.IDX_NAME, area.name());
            cs.setString(SpCreate.IDX_CODE, area.code());
            if (area.description() == null) cs.setNull(SpCreate.IDX_DESCRIPTION, Types.NVARCHAR); else cs.setString(SpCreate.IDX_DESCRIPTION, area.description());
            if (area.userInChargeId() == null) cs.setNull(SpCreate.IDX_USER_IN_CHARGE, Types.INTEGER); else cs.setInt(SpCreate.IDX_USER_IN_CHARGE, area.userInChargeId());

            cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null) {
                int update = cs.getUpdateCount();
                if (update == -1) break;
                if (!cs.getMoreResults()) break;
                rs = cs.getResultSet();
            }
            if (rs != null && rs.next()) {
                try {
                    return rs.getInt("NewAreaID");
                } catch (SQLException ignore) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }
}
