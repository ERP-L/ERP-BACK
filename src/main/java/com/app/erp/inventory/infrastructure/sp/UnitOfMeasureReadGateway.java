package com.app.erp.inventory.infrastructure.sp;

import com.app.erp.inventory.application.internal.messages.results.UnitOfMeasureResult;
import com.app.erp.inventory.application.internal.port.UnitOfMeasureReadPort;
import com.app.erp.shared.exceptions.ApplicationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class UnitOfMeasureReadGateway implements UnitOfMeasureReadPort {

    private final DataSource ds;

    public UnitOfMeasureReadGateway(DataSource ds) { this.ds = Objects.requireNonNull(ds); }

    @Override
    public List<UnitOfMeasureResult> getAll() {
        final String call = "{ call [inventory].[usp_UnitOfMeasure_GetAll]() }";
        try (Connection con = ds.getConnection(); CallableStatement cs = con.prepareCall(call)) {
            boolean hasRs = cs.execute();
            List<UnitOfMeasureResult> out = new ArrayList<>();
            if (hasRs) try (ResultSet rs = cs.getResultSet()) {
                while (rs.next()) {
                    UnitOfMeasureResult r = new UnitOfMeasureResult();
                    r.setUomId(rs.getInt("UOMID"));
                    r.setUomCode(rs.getString("UOMCode"));
                    r.setUomName(rs.getString("UOMName"));
                    r.setDecimalPlaces(rs.getInt("DecimalPlaces"));
                    Timestamp ts = rs.getTimestamp("CreatedUtc");
                    r.setCreatedUtc(ts == null ? null : ts.toInstant().atOffset(ZoneOffset.UTC));
                    out.add(r);
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new ApplicationException("Error leyendo unidades de medida", ex);
        }
    }
}
