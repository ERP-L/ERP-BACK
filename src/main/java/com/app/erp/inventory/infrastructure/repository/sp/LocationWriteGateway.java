package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.commands.CreateLocationCommand;
import com.app.erp.inventory.application.dtos.results.CreateLocationResult;
import com.app.erp.inventory.application.port.LocationWritePort;
import com.app.erp.inventory.infrastructure.mapper.LocationRowMapper;
import com.app.erp.inventory.infrastructure.error.DbErrorTranslator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LocationWriteGateway implements LocationWritePort {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall createCall;

    public LocationWriteGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        this.createCall = new SimpleJdbcCall(this.jdbc)
                .withSchemaName("inventory")
                .withProcedureName("usp_Location_Create")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("WarehouseID", Types.INTEGER),
                        new SqlParameter("Code", Types.NVARCHAR),
                        new SqlParameter("ParentID", Types.INTEGER),
                        new SqlParameter("AllowStock", Types.BIT),
                        new SqlOutParameter("LocationID", Types.INTEGER)
                )
                .returningResultSet("result", new LocationRowMapper());
    }

    @Override
    public CreateLocationResult createLocation(CreateLocationCommand cmd) {
        Map<String, Object> in = new HashMap<>();
        in.put("WarehouseID", cmd.getWarehouseId());
        in.put("Code", cmd.getCode());
        in.put("ParentID", cmd.getParentId() == null ? null : cmd.getParentId());
        in.put("AllowStock", cmd.getAllowStock() == null ? Boolean.TRUE : cmd.getAllowStock());

        try {
            Map<String, Object> out = createCall.execute(in);

            @SuppressWarnings("unchecked")
            List<CreateLocationResult> rows = (List<CreateLocationResult>) out.get("result");
            if (rows != null && !rows.isEmpty()) return rows.get(0);

            Integer id = (Integer) out.get("LocationID");
            if (id != null) {
                return jdbc.queryForObject(
                        "SELECT LocationID, WarehouseID, ParentID, Code, AllowStock, CreatedUtc FROM inventory.Location WHERE LocationID = ?",
                        new LocationRowMapper(), id
                );
            }

            throw new RuntimeException("No se recibió resultado del SP usp_Location_Create");
        } catch (DataAccessException dae) {
            Throwable cause = dae.getCause();
            if (cause instanceof java.sql.SQLException) throw DbErrorTranslator.translate((java.sql.SQLException) cause);
            throw dae;
        }
    }
}
