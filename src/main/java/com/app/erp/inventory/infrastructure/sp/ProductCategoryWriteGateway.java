package com.app.erp.inventory.infrastructure.sp;

import com.app.erp.inventory.application.internal.port.ProductCategoryWritePort;
import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.inventory.application.internal.messages.results.CreateProductCategoryResult;
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
            String msg = dae.getRootCause() == null ? dae.getMessage() : dae.getRootCause().getMessage();
            Integer code = extractErrorCode(msg);
            if (code != null) {
                switch (code) {
                    case 50001:
                        throw new com.app.erp.shared.exceptions.NotFoundException(msg);
                    case 50002:
                        throw new com.app.erp.shared.exceptions.ResourceConflictException(msg);
                    default:
                        throw new ApplicationException(msg, dae);
                }
            }
            throw new ApplicationException(msg, dae);
        }
    }

    private Integer extractErrorCode(String msg) {
        if (msg == null) return null;
        if (msg.contains("50001")) return 50001;
        if (msg.contains("50002")) return 50002;
        return null;
    }
}
