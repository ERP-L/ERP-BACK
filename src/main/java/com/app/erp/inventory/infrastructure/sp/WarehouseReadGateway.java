package com.app.erp.inventory.infrastructure.sp;

import com.app.erp.inventory.application.internal.port.WarehouseReadPort;
import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;
import com.app.erp.inventory.infrastructure.mappers.WarehouseRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WarehouseReadGateway implements WarehouseReadPort {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall listCall;

    public WarehouseReadGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        this.listCall = new SimpleJdbcCall(this.jdbc)
                .withSchemaName("inventory")
                .withProcedureName("usp_Warehouse_ListByCompany")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("CompanyID", Types.INTEGER),
                        new SqlParameter("OnlyActive", Types.BIT),
                        new SqlParameter("BranchID", Types.INTEGER)
                )
                .returningResultSet("result", new WarehouseRowMapper());
    }

    @Override
    public List<CreateWarehouseResult> listWarehousesByCompany(int companyId, Boolean onlyActive, Integer branchId) {
        Map<String, Object> in = new HashMap<>();
        in.put("CompanyID", companyId);
        in.put("OnlyActive", onlyActive == null ? null : onlyActive);
        in.put("BranchID", branchId);

        Map<String, Object> out = listCall.execute(in);

        @SuppressWarnings("unchecked")
        List<CreateWarehouseResult> rows = (List<CreateWarehouseResult>) out.get("result");
        return rows == null ? java.util.Collections.emptyList() : rows;
    }
}
