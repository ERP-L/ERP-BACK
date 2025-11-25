package com.app.erp.inventoryrefactor.supplier.infrastructure.repository.sp;

import com.app.erp.inventoryrefactor.common.PageResult;
import org.springframework.stereotype.Repository;
import com.app.erp.inventoryrefactor.supplier.domain.ProductSummary;
import com.app.erp.inventoryrefactor.supplier.domain.Supplier;
import com.app.erp.inventoryrefactor.supplier.infrastructure.mapper.SupplierDbMapper;
import com.app.erp.inventoryrefactor.supplier.infrastructure.repository.SupplierRepositoryPort;
import com.app.erp.shared.infrastructure.error.DbErrorTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SupplierRepositorySp implements SupplierRepositoryPort {

    private final DataSource dataSource;

    public SupplierRepositorySp(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static class SpCreate {
        static final String CALL = "{ call [inventory].[usp_Supplier_Create](?, ?, ?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_SUPPLIER_NAME = 2;
        static final int IDX_TAX_NUMBER = 3;
        static final int IDX_CONTACT = 4;
        static final int IDX_PHONE = 5;
        static final int IDX_EMAIL = 6;
        static final int IDX_ADDRESS = 7;
        static final int IDX_NOTES = 8;
    }

    private static class SpGetAll {
        // SP signature: (@CompanyID, @Search, @IsActive, @ProductIDs, @Page, @PageSize)
        static final String CALL = "{ call [inventory].[usp_Supplier_GetAll](?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_SEARCH = 2;
        static final int IDX_ACTIVE = 3;
        static final int IDX_PRODUCTIDS = 4;
        static final int IDX_PAGE = 5;
        static final int IDX_PAGESIZE = 6;
        // resultsets: 1 -> total count, 2 -> rows
    }

    private static class SpGetProducts {
        static final String CALL = "{ call [inventory].[usp_Supplier_GetProducts](?) }";
        static final int IDX_SUPPLIER_ID = 1;
    }

    private static class SpAddProduct {
        static final String CALL = "{ call [inventory].[usp_SupplierProduct_Add](?, ?) }";
        static final int IDX_SUPPLIER_ID = 1;
        static final int IDX_PRODUCT_ID = 2;
    }

    @Override
    public int create(Supplier supplier) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpCreate.CALL)) {
            cs.setInt(SpCreate.IDX_COMPANY_ID, supplier.companyId());
            cs.setString(SpCreate.IDX_SUPPLIER_NAME, supplier.supplierName());
            cs.setString(SpCreate.IDX_TAX_NUMBER, supplier.taxNumber());
            cs.setString(SpCreate.IDX_CONTACT, supplier.contactName());
            cs.setString(SpCreate.IDX_PHONE, supplier.phone());
            cs.setString(SpCreate.IDX_EMAIL, supplier.email());
            cs.setString(SpCreate.IDX_ADDRESS, supplier.address());
            cs.setString(SpCreate.IDX_NOTES, supplier.notes());

            boolean hasResults = cs.execute();
            // Standard pattern: iterate result sets until we get a ResultSet, which should be the SELECT SCOPE_IDENTITY().
            // Do not call getMoreResults() unnecessarily; loop reading current ResultSet then advance.
            ResultSet rs = cs.getResultSet();
            while (rs == null) {
                int update = cs.getUpdateCount();
                if (update == -1) break; // no more results
                if (!cs.getMoreResults()) break;
                rs = cs.getResultSet();
            }

            if (rs != null) {
                try (ResultSet r = rs) {
                    if (r.next()) {
                        try {
                            return r.getInt("NewSupplierID");
                        } catch (SQLException ignore) {
                            return r.getInt(1);
                        }
                    }
                }
            }
            return -1;
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public PageResult<Supplier> findAll(int companyId, String search, Boolean active, String productIds, int page, int pageSize) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpGetAll.CALL)) {
            cs.setInt(SpGetAll.IDX_COMPANY_ID, companyId);
            cs.setString(SpGetAll.IDX_SEARCH, search);
            if (active == null) cs.setNull(SpGetAll.IDX_ACTIVE, Types.BIT); else cs.setBoolean(SpGetAll.IDX_ACTIVE, active);
            if (productIds == null || productIds.isBlank()) cs.setNull(SpGetAll.IDX_PRODUCTIDS, Types.VARCHAR); else cs.setString(SpGetAll.IDX_PRODUCTIDS, productIds);
            cs.setInt(SpGetAll.IDX_PAGE, page);
            cs.setInt(SpGetAll.IDX_PAGESIZE, pageSize);

            cs.execute();
            List<Supplier> items = new ArrayList<>();
            long total = 0;
            // Find first ResultSet (skip update counts)
            ResultSet rs = cs.getResultSet();
            while (rs == null) {
                int update = cs.getUpdateCount();
                if (update == -1) break;
                if (!cs.getMoreResults()) break;
                rs = cs.getResultSet();
            }

            if (rs == null) {
                return new PageResult<>(0, items);
            }

            ResultSetMetaData meta = rs.getMetaData();
            boolean singleColumn = meta.getColumnCount() == 1;
            String firstLabel = meta.getColumnLabel(1);

            if (singleColumn && firstLabel != null && firstLabel.equalsIgnoreCase("TotalCount")) {
                // first RS is total
                if (rs.next()) total = rs.getLong(1);
                // move to next RS for data
                if (cs.getMoreResults()) rs = cs.getResultSet(); else rs = null;
                if (rs != null) {
                    while (rs.next()) items.add(SupplierDbMapper.fromResultSet(rs));
                }
                if (total == 0) total = items.size();
            } else {
                // first RS is data rows
                while (rs.next()) items.add(SupplierDbMapper.fromResultSet(rs));
                total = items.size();
            }

            return new PageResult<>(total, items);
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public List<ProductSummary> findProductsBySupplier(int supplierId) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpGetProducts.CALL)) {
            cs.setInt(SpGetProducts.IDX_SUPPLIER_ID, supplierId);
            boolean has = cs.execute();
            List<ProductSummary> out = new ArrayList<>();
            try (ResultSet rs = cs.getResultSet()) {
                if (rs != null) {
                    while (rs.next()) {
                        out.add(SupplierDbMapper.productSummaryFromResultSet(rs));
                    }
                }
            }
            return out;
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    @Override
    public void addProductToSupplier(int supplierId, int productId) {
        try (Connection con = dataSource.getConnection(); CallableStatement cs = con.prepareCall(SpAddProduct.CALL)) {
            cs.setInt(SpAddProduct.IDX_SUPPLIER_ID, supplierId);
            cs.setInt(SpAddProduct.IDX_PRODUCT_ID, productId);
            cs.execute();
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }
}
