package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.port.ProductCategoryReadPort;
import com.app.erp.inventory.application.dtos.results.ReparentProductCategoryResult;
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
public class ProductCategoryReadGateway implements ProductCategoryReadPort {

    private final DataSource ds;

    public ProductCategoryReadGateway(DataSource ds) { this.ds = Objects.requireNonNull(ds); }

    @Override
    public List<ReparentProductCategoryResult> getAllByCompany(int companyId, boolean onlyActive) {
        final String call = "{ call [inventory].[usp_ProductCategory_GetAllByCompany](?, ?) }";
        try (Connection con = ds.getConnection(); CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, companyId);
            cs.setBoolean(2, onlyActive);

            boolean hasRs = cs.execute();
            List<ReparentProductCategoryResult> out = new ArrayList<>();
            if (hasRs) try (ResultSet rs = cs.getResultSet()) {
                while (rs.next()) {
                    int cid = rs.getInt("CategoryID");
                    String name = rs.getString("CategoryName");
                    String desc = rs.getString("Description");
                    Integer parent = rs.getObject("ParentCategoryID") == null ? null : rs.getInt("ParentCategoryID");
                    boolean active = rs.getBoolean("IsActive");
                    Timestamp ts = rs.getTimestamp("CreatedUtc");
                    OffsetDateTime created = ts == null ? null : ts.toInstant().atOffset(ZoneOffset.UTC);
                    int comp = rs.getInt("CompanyID");

                    ReparentProductCategoryResult r = new ReparentProductCategoryResult();
                    r.setCategoryId(cid);
                    r.setCategoryName(name);
                    r.setDescription(desc);
                    r.setParentCategoryId(parent);
                    r.setIsActive(active);
                    r.setCreatedUtc(created);
                    r.setCompanyId(comp);
                    out.add(r);
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new ApplicationException("Error leyendo categorías", ex);
        }
    }
}
