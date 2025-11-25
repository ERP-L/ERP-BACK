package com.app.erp.finances.costcenter.infrastructure.repository.sp;

import com.app.erp.finances.costcenter.domain.CostCenter;
import com.app.erp.finances.costcenter.infrastructure.mapper.CostCenterDbMapper;
import com.app.erp.finances.costcenter.infrastructure.repository.CostCenterRepositoryPort;
import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.shared.infrastructure.error.DbErrorTranslator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class CostCenterRepositorySp implements CostCenterRepositoryPort {

    private final DataSource dataSource;

    public CostCenterRepositorySp(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    private static class SpCreate {
        static final String CALL = "{ call [finance].[usp_CostCenter_Create](?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_AREA_ID = 2;
        static final int IDX_CODE = 3;
        static final int IDX_NAME = 4;
        static final int IDX_DESCRIPTION = 5;
        static final int IDX_USER_CREATED = 6;
    }

    private static class SpGetById {
        static final String CALL = "{ call [finance].[usp_CostCenter_GetById](?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_COSTCENTER_ID = 2;
    }

    private static class SpSearch {
        static final String CALL = "{ call [finance].[usp_CostCenter_Search](?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_AREA_ID = 2;
        static final int IDX_BRANCH_ID = 3;
        static final int IDX_SEARCH = 4;
        static final int IDX_PAGE = 5;
        static final int IDX_PAGESIZE = 6;
    }

    @Override
    public int create(int companyId, int userCreated, CostCenter cc) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpCreate.CALL)) {
            cs.setInt(SpCreate.IDX_COMPANY_ID, companyId);
            cs.setInt(SpCreate.IDX_AREA_ID, cc.areaId());
            cs.setString(SpCreate.IDX_CODE, cc.code());
            cs.setString(SpCreate.IDX_NAME, cc.name());
            if (cc.description() == null) cs.setNull(SpCreate.IDX_DESCRIPTION, Types.NVARCHAR); else cs.setString(SpCreate.IDX_DESCRIPTION, cc.description());
            if (userCreated <= 0) cs.setNull(SpCreate.IDX_USER_CREATED, Types.INTEGER); else cs.setInt(SpCreate.IDX_USER_CREATED, userCreated);

            cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null) {
                int update = cs.getUpdateCount();
                if (update == -1) break;
                if (!cs.getMoreResults()) break;
                rs = cs.getResultSet();
            }
            if (rs != null && rs.next()) {
                try { return rs.getInt("NewCostCenterID"); } catch (SQLException ignored) { return rs.getInt(1); }
            }
            return -1;
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public CostCenter findById(int companyId, int costCenterId) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpGetById.CALL)) {
            cs.setInt(SpGetById.IDX_COMPANY_ID, companyId);
            cs.setInt(SpGetById.IDX_COSTCENTER_ID, costCenterId);
            cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null) {
                int update = cs.getUpdateCount();
                if (update == -1) break;
                if (!cs.getMoreResults()) break;
                rs = cs.getResultSet();
            }
            if (rs != null && rs.next()) return CostCenterDbMapper.fromResultSet(rs);
            return null;
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public PageResult<CostCenter> search(int companyId, Integer areaId, Integer branchId, String search, int page, int pageSize) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpSearch.CALL)) {
            cs.setInt(SpSearch.IDX_COMPANY_ID, companyId);
            if (areaId == null) cs.setNull(SpSearch.IDX_AREA_ID, Types.INTEGER); else cs.setInt(SpSearch.IDX_AREA_ID, areaId);
            if (branchId == null) cs.setNull(SpSearch.IDX_BRANCH_ID, Types.INTEGER); else cs.setInt(SpSearch.IDX_BRANCH_ID, branchId);
            cs.setString(SpSearch.IDX_SEARCH, search);
            cs.setInt(SpSearch.IDX_PAGE, page);
            cs.setInt(SpSearch.IDX_PAGESIZE, pageSize);

            cs.execute();
            List<CostCenter> items = new ArrayList<>();
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
                if (rs != null) { while (rs.next()) items.add(CostCenterDbMapper.fromResultSet(rs)); }
                if (total == 0) total = items.size();
            } else {
                while (rs.next()) items.add(CostCenterDbMapper.fromResultSet(rs));
                total = items.size();
            }

            return new PageResult<>(total, items);
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }
}
