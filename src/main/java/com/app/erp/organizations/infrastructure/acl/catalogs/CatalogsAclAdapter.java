package com.app.erp.organizations.infrastructure.acl.catalogs;

import com.app.erp.organizations.application.port.CatalogsQueryGateway;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

public class CatalogsAclAdapter implements CatalogsQueryGateway {

    private final DataSource dataSource;

    public CatalogsAclAdapter(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public boolean existsUbigeo(String ubigeoId) {
        final String sql = "SELECT 1 FROM [cfg].[Ubigeo] WHERE UbigeoID = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ubigeoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // Si prefieres tratar errores como false, cámbialo; yo propago para ver el problema en logs.
            throw new RuntimeException("Error consultando Ubigeo en cfg.Ubigeo", e);
        }
    }
}
