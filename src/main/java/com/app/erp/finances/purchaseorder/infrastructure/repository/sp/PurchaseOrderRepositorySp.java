package com.app.erp.finances.purchaseorder.infrastructure.repository.sp;

import com.app.erp.finances.purchaseorder.domain.PurchaseOrder;
import com.app.erp.finances.purchaseorder.domain.PurchaseOrderProduct;
import com.app.erp.finances.purchaseorder.infrastructure.mapper.PurchaseOrderDbMapper;
import com.app.erp.finances.purchaseorder.infrastructure.repository.PurchaseOrderRepositoryPort;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderSearchResponse;
import com.app.erp.shared.dtos.PagedResponse;
import com.app.erp.shared.infrastructure.error.DbErrorTranslator;
import org.springframework.dao.DataAccessException;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Repository
public class PurchaseOrderRepositorySp implements PurchaseOrderRepositoryPort {

    private final DataSource dataSource;

    private static class SpCreate {
        static final String CALL = "{ call finance.usp_PurchaseOrder_CreateFull(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_COSTCENTER_ID = 2;
        static final int IDX_PURCHASETYPE_ID = 3;
        static final int IDX_CODIGO = 4;
        static final int IDX_RUC = 5;
        static final int IDX_NOMBRE = 6;
        static final int IDX_DIRECCION = 7;
        static final int IDX_TELEFONO = 8;
        static final int IDX_FECHA_EMISION = 9;
        static final int IDX_USER_CREATED = 10;
        static final int IDX_PRODUCTS = 11;
        static final int IDX_PURCHASE_ORDER_ID_OUT = 12;
    }

    private static class SpGetById {
        static final String CALL = "{ call finance.usp_PurchaseOrder_GetById(?) }";
        static final int IDX_PURCHASE_ORDER_ID = 1;
    }

    private static class SpSearch {
        static final String CALL = "{ call finance.usp_PurchaseOrder_Search(?, ?, ?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_AREA_ID = 2;
        static final int IDX_COSTCENTER_ID = 3;
        static final int IDX_PURCHASETYPE_CODE = 4;
        static final int IDX_STATUS_CODE = 5;
        static final int IDX_SEARCH_TERM = 6;
        static final int IDX_PAGE_NUMBER = 7;
        static final int IDX_PAGE_SIZE = 8;
    }

    private static class SpGetProducts {
        static final String CALL = "{ call finance.usp_PurchaseOrderProduct_GetAllByOrder(?) }";
        static final int IDX_PURCHASE_ORDER_ID = 1;
    }

    public PurchaseOrderRepositorySp(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public int create(PurchaseOrder purchaseOrder, List<PurchaseOrderProduct> products) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpCreate.CALL)) {

            SQLServerDataTable productsTable = new SQLServerDataTable();
            productsTable.addColumnMetadata("ProductID", java.sql.Types.INTEGER);
            productsTable.addColumnMetadata("WarehouseID", java.sql.Types.INTEGER);
            productsTable.addColumnMetadata("Cantidad", java.sql.Types.DECIMAL);
            productsTable.addColumnMetadata("UnitCost", java.sql.Types.DECIMAL);

            for (PurchaseOrderProduct p : products) {
                productsTable.addRow(p.productId(), p.warehouseId(), p.cantidad(), p.unitCost());
            }

            cs.setInt(SpCreate.IDX_COMPANY_ID, purchaseOrder.companyId());
            cs.setInt(SpCreate.IDX_COSTCENTER_ID, purchaseOrder.costCenterId());
            cs.setInt(SpCreate.IDX_PURCHASETYPE_ID, purchaseOrder.purchaseTypeId());
            cs.setString(SpCreate.IDX_CODIGO, purchaseOrder.codigo());
            cs.setString(SpCreate.IDX_RUC, purchaseOrder.ruc());
            cs.setString(SpCreate.IDX_NOMBRE, purchaseOrder.nombre());
            cs.setString(SpCreate.IDX_DIRECCION, purchaseOrder.direccion());
            cs.setString(SpCreate.IDX_TELEFONO, purchaseOrder.telefono());
            cs.setDate(SpCreate.IDX_FECHA_EMISION, java.sql.Date.valueOf(purchaseOrder.fechaEmision()));
            cs.setInt(SpCreate.IDX_USER_CREATED, purchaseOrder.userCreated());
            cs.setObject(SpCreate.IDX_PRODUCTS, productsTable);
            cs.registerOutParameter(SpCreate.IDX_PURCHASE_ORDER_ID_OUT, java.sql.Types.INTEGER);

            cs.execute();

            return cs.getInt(SpCreate.IDX_PURCHASE_ORDER_ID_OUT);

        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }

    @Override
    public Optional<PurchaseOrder> findById(int id) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpGetById.CALL)) {

            cs.setInt(SpGetById.IDX_PURCHASE_ORDER_ID, id);
            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }
            if (rs != null && rs.next()) {
                PurchaseOrder order = PurchaseOrderDbMapper.toDomain(rs);
                return Optional.of(order);
            }
            return Optional.empty();

        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }

    @Override
    public List<PurchaseOrderProduct> findProductsByPurchaseOrderId(int purchaseOrderId) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpGetProducts.CALL)) {

            cs.setInt(SpGetProducts.IDX_PURCHASE_ORDER_ID, purchaseOrderId);
            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }
            List<PurchaseOrderProduct> products = new ArrayList<>();
            if (rs != null) {
                while (rs.next()) {
                    products.add(new PurchaseOrderProduct(
                            rs.getInt("PurchaseOrderProductID"),
                            rs.getInt("PurchaseOrderID"),
                            rs.getInt("ProductID"),
                            rs.getInt("WarehouseID"),
                            rs.getBigDecimal("Cantidad"),
                            rs.getBigDecimal("UnitCost")
                    ));
                }
            }
            return products;

        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }

    @Override
    public PagedResponse<PurchaseOrderSearchResponse> search(int companyId, Integer areaId, Integer costCenterId, String purchaseTypeCode, String statusCode, String searchTerm, int pageNumber, int pageSize) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpSearch.CALL)) {

            cs.setInt(SpSearch.IDX_COMPANY_ID, companyId);
            if (areaId != null) cs.setInt(SpSearch.IDX_AREA_ID, areaId); else cs.setNull(SpSearch.IDX_AREA_ID, java.sql.Types.INTEGER);
            if (costCenterId != null) cs.setInt(SpSearch.IDX_COSTCENTER_ID, costCenterId); else cs.setNull(SpSearch.IDX_COSTCENTER_ID, java.sql.Types.INTEGER);
            if (purchaseTypeCode != null) cs.setString(SpSearch.IDX_PURCHASETYPE_CODE, purchaseTypeCode); else cs.setNull(SpSearch.IDX_PURCHASETYPE_CODE, java.sql.Types.VARCHAR);
            if (statusCode != null) cs.setString(SpSearch.IDX_STATUS_CODE, statusCode); else cs.setNull(SpSearch.IDX_STATUS_CODE, java.sql.Types.VARCHAR);
            if (searchTerm != null) cs.setString(SpSearch.IDX_SEARCH_TERM, searchTerm); else cs.setNull(SpSearch.IDX_SEARCH_TERM, java.sql.Types.NVARCHAR);
            cs.setInt(SpSearch.IDX_PAGE_NUMBER, pageNumber);
            cs.setInt(SpSearch.IDX_PAGE_SIZE, pageSize);

            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            List<PurchaseOrderSearchResponse> items = new ArrayList<>();
            long totalRows = 0;

            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }

            if (rs != null) {
                while (rs.next()) {
                    items.add(PurchaseOrderDbMapper.toSearchResponse(rs));
                }
                if (!items.isEmpty()) {
                    PurchaseOrderSearchResponse last = items.get(items.size() - 1);
                    totalRows = last.totalRows() != null ? last.totalRows() : items.size();
                }
            }

            int totalPages = (pageSize > 0) ? (int) Math.ceil((double) totalRows / pageSize) : 0;
            return new PagedResponse<>(items, pageNumber, pageSize, totalRows, totalPages);

        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }
}
