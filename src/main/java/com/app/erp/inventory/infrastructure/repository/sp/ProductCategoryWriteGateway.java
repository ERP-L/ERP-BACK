package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.port.ProductCategoryWritePort;
import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.inventory.infrastructure.error.DbErrorTranslator;
import com.app.erp.inventory.infrastructure.mapper.ProductCategoryDbMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ProductCategoryWriteGateway implements ProductCategoryWritePort {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall createCall;

    public ProductCategoryWriteGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public com.app.erp.inventory.application.dtos.results.ReparentProductCategoryResult reparentProductCategory(int companyId, int categoryId, Integer newParentCategoryId) {
        final String call = "{ call [inventory].[usp_ProductCategory_Reparent](?, ?, ?) }";
        try (java.sql.Connection con = jdbc.getDataSource().getConnection();
             java.sql.CallableStatement cs = con.prepareCall(call)) {

            cs.setInt(1, companyId);
            cs.setInt(2, categoryId);
            if (newParentCategoryId == null) cs.setNull(3, Types.INTEGER);
            else cs.setInt(3, newParentCategoryId);

            boolean hasRs = cs.execute();
            if (hasRs) try (java.sql.ResultSet rs = cs.getResultSet()) {
                if (rs.next()) {
                    return ProductCategoryDbMapper.toReparentResult(rs);
                }
            }
            throw new ApplicationException("SP did not return updated category row");
        } catch (java.sql.SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @PostConstruct
    void init() {
        this.createCall = new SimpleJdbcCall(this.jdbc)
                .withSchemaName("inventory")
                .withProcedureName("usp_ProductCategory_Create")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("CompanyID", Types.INTEGER),
                        new SqlParameter("CategoryName", Types.NVARCHAR),
                        new SqlParameter("Description", Types.NVARCHAR),
                        new SqlParameter("ParentCategoryID", Types.INTEGER),
                        new SqlParameter("IsActive", Types.BIT)
                );
    }

    @Override
    public Integer createProductCategory(int companyId, String categoryName, String description, Integer parentCategoryId, boolean isActive) {
        Map<String, Object> in = new HashMap<>();
        in.put("CompanyID", companyId);
        in.put("CategoryName", categoryName);
        in.put("Description", description);
        in.put("ParentCategoryID", parentCategoryId);
        in.put("IsActive", isActive);

        try {
            Map<String, Object> out = createCall.execute(in);
            // The SP returns a single row with CategoryID
            // SimpleJdbcCall when no returningResultSet will not return the rowset; use query as fallback
            Number newId = jdbc.queryForObject(
                    "SELECT TOP 1 CategoryID FROM inventory.ProductCategory WHERE CompanyID = ? AND CategoryName = ? ORDER BY CategoryID DESC",
                    Number.class, companyId, categoryName);
            return newId == null ? null : newId.intValue();
        } catch (org.springframework.dao.DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof java.sql.SQLException sqlEx) {
                throw DbErrorTranslator.translate(sqlEx);
            }
            String msg = dae.getMessage();
            throw new ApplicationException(msg != null ? msg : "Database error", dae);
        }
    }
}
