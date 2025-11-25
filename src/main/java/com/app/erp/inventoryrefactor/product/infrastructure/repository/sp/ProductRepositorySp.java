package com.app.erp.inventoryrefactor.product.infrastructure.repository.sp;

import com.app.erp.inventoryrefactor.product.domain.Product;
import com.app.erp.inventoryrefactor.product.infrastructure.mapper.ProductDbMapper;
import com.app.erp.inventoryrefactor.product.infrastructure.repository.ProductRepositoryPort;
import com.app.erp.inventory.infrastructure.error.DbErrorTranslator;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Repository
public class ProductRepositorySp implements ProductRepositoryPort {

    private final DataSource ds;

    public ProductRepositorySp(DataSource ds) { this.ds = Objects.requireNonNull(ds); }

    private static final class SpGetById {
        static final String CALL = "{ call [inventory].[usp_Product_GetById](?) }";
        static final int IDX_PRODUCT_ID = 1;
    }

    @Override
    public Product findById(int productId) {
        try (Connection con = ds.getConnection(); CallableStatement cs = con.prepareCall(SpGetById.CALL)) {
            cs.setInt(SpGetById.IDX_PRODUCT_ID, productId);
            boolean has = cs.execute();
            if (!has) return null;
            try (ResultSet rs = cs.getResultSet()) {
                if (rs.next()) {
                    return ProductDbMapper.fromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }
}
