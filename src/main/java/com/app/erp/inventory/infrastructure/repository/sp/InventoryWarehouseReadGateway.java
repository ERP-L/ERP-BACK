package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.results.InventoryProductResult;
import com.app.erp.inventory.application.port.InventoryWarehouseReadPort;
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
public class InventoryWarehouseReadGateway implements InventoryWarehouseReadPort {

    private final DataSource ds;

    public InventoryWarehouseReadGateway(DataSource ds) { this.ds = Objects.requireNonNull(ds); }

    @Override
    public List<InventoryProductResult> listProductsInWarehouse(int companyId, int warehouseId, Integer productId, Integer categoryId, String search, String orderBy, int pageNumber, int pageSize) {
        final String call = "{ call [inventory].[usp_Inventory_ListProductsInWarehouse](?,?,?,?,?,?,?,?) }";
        try (Connection con = ds.getConnection(); CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, companyId);
            cs.setInt(2, warehouseId);

            if (productId == null) cs.setNull(3, java.sql.Types.INTEGER); else cs.setInt(3, productId);
            if (categoryId == null) cs.setNull(4, java.sql.Types.INTEGER); else cs.setInt(4, categoryId);
            if (search == null) cs.setNull(5, java.sql.Types.NVARCHAR); else cs.setString(5, search);
            if (orderBy == null) cs.setString(6, "ProductName"); else cs.setString(6, orderBy);
            cs.setInt(7, pageNumber);
            cs.setInt(8, pageSize);

            boolean hasRs = cs.execute();
            List<InventoryProductResult> out = new ArrayList<>();
            if (hasRs) try (ResultSet rs = cs.getResultSet()) {
                while (rs.next()) {
                    InventoryProductResult p = new InventoryProductResult();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setSku(rs.getString("SKU"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setCategoryId(rs.getObject("CategoryID") == null ? null : rs.getInt("CategoryID"));
                    p.setUomId(rs.getObject("UOMID") == null ? null : rs.getInt("UOMID"));
                    p.setIsSerialized(rs.getBoolean("IsSerialized"));
                    p.setIsBatchControlled(rs.getBoolean("IsBatchControlled"));
                    p.setStatus(rs.getObject("Status") == null ? null : rs.getInt("Status"));
                    Timestamp created = rs.getTimestamp("CreatedUtc");
                    p.setCreatedUtc(created == null ? null : created.toInstant().atOffset(ZoneOffset.UTC));
                    Timestamp updated = rs.getTimestamp("UpdatedUtc");
                    p.setUpdatedUtc(updated == null ? null : updated.toInstant().atOffset(ZoneOffset.UTC));
                    p.setCompanyId(rs.getInt("CompanyID"));
                    p.setTrackingMode(rs.getString("TrackingMode"));
                    p.setTrackingLabel(rs.getString("TrackingLabel"));
                    p.setAvgCost(rs.getBigDecimal("AvgCost"));
                    p.setQuantity(rs.getBigDecimal("Quantity"));
                    p.setReserved(rs.getBigDecimal("Reserved"));
                    p.setLocationsStr(rs.getString("LocationsStr"));
                    out.add(p);
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new ApplicationException("Error leyendo inventario por warehouse", ex);
        }
    }
}
