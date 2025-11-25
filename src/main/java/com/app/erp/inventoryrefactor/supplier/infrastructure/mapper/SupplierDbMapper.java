package com.app.erp.inventoryrefactor.supplier.infrastructure.mapper;

import com.app.erp.inventoryrefactor.supplier.domain.ProductSummary;
import com.app.erp.inventoryrefactor.supplier.domain.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierDbMapper {

    public static Supplier fromResultSet(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("SupplierID"),
                rs.getInt("CompanyID"),
                rs.getString("SupplierName"),
                rs.getString("TaxNumber"),
                rs.getString("ContactName"),
                rs.getString("Phone"),
                rs.getString("Email"),
                rs.getString("Address"),
                rs.getString("Notes"),
                rs.getBoolean("IsActive")
        );
    }

    public static ProductSummary productSummaryFromResultSet(ResultSet rs) throws SQLException {
        return new ProductSummary(
                rs.getInt("ProductID"),
                rs.getString("SKU"),
                rs.getString("ProductName")
        );
    }
}
