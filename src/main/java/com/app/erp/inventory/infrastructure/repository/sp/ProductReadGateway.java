package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.results.ProductResult;
import com.app.erp.inventory.application.port.ProductReadPort;
import com.app.erp.shared.exceptions.ApplicationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductReadGateway implements ProductReadPort {

    private final DataSource ds;

    public ProductReadGateway(DataSource ds) { this.ds = Objects.requireNonNull(ds); }

    @Override
    public List<ProductResult> getAllByCompany(int companyId) {
        final String call = "{ call [inventory].[usp_Product_GetAllByCompany](?) }";
        try (Connection con = ds.getConnection(); CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, companyId);

            boolean hasRs = cs.execute();
            List<ProductResult> out = new ArrayList<>();
            if (hasRs) try (ResultSet rs = cs.getResultSet()) {
                while (rs.next()) {
                    ProductResult p = new ProductResult();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setSku(rs.getString("SKU"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setCategoryId(rs.getObject("CategoryID") == null ? null : rs.getInt("CategoryID"));
                    p.setUomId(rs.getObject("UOMID") == null ? null : rs.getInt("UOMID"));
                    p.setIsSerialized(rs.getBoolean("IsSerialized"));
                    p.setIsBatchControlled(rs.getBoolean("IsBatchControlled"));
                    p.setReorderLevel(rs.getBigDecimal("ReorderLevel"));
                    p.setLeadTimeDays(rs.getObject("LeadTimeDays") == null ? null : rs.getInt("LeadTimeDays"));
                    p.setWeight(rs.getBigDecimal("Weight"));
                    p.setVolume(rs.getBigDecimal("Volume"));
                    p.setStatus(rs.getObject("Status") == null ? null : rs.getInt("Status"));
                    Timestamp created = rs.getTimestamp("CreatedUtc");
                    Timestamp updated = rs.getTimestamp("UpdatedUtc");
                    p.setCreatedUtc(created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC));
                    p.setUpdatedUtc(updated == null ? null : updated.toInstant().atOffset(ZoneOffset.UTC));
                    p.setCompanyId(rs.getInt("CompanyID"));
                    out.add(p);
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new ApplicationException("Error leyendo productos", ex);
        }
    }
}
