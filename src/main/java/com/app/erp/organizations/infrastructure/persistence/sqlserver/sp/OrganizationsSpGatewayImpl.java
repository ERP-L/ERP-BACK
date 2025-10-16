package com.app.erp.organizations.infrastructure.persistence.sqlserver.sp;
import com.app.erp.organizations.application.port.OrganizationsCommandGateway;
import com.app.erp.organizations.application.port.OrganizationsQueryGateway;
import com.app.erp.organizations.domain.Branch;
import com.app.erp.organizations.domain.valueobjects.AddressLine;
import com.app.erp.organizations.domain.valueobjects.BranchName;
import com.app.erp.organizations.domain.valueobjects.UbigeoId;
import com.app.erp.shared.exceptions.ApplicationException;

import javax.sql.DataSource;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Adapter JDBC que invoca [core].[sp_RegisterBranch] y luego lee la fila creada
 * para hidratar la entidad completa.
 */
public class OrganizationsSpGatewayImpl implements OrganizationsCommandGateway, OrganizationsQueryGateway {

    private final DataSource dataSource;

    public OrganizationsSpGatewayImpl(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public Branch registerBranch(Branch newBranch) {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);

            Integer newId = callSpRegisterBranch(con, newBranch);
            if (newId == null || newId <= 0) {
                con.rollback();
                throw new ApplicationException("SP no devolvió BranchID válido");
            }

            Branch persisted = fetchBranchById(con, newId);
            con.commit();
            return persisted;
        } catch (SQLException ex) {
            throw new ApplicationException("Error persistiendo Branch vía SP", ex);
        }
    }

    private Integer callSpRegisterBranch(Connection con, Branch b) throws SQLException {
        // Ajusta el nombre del SP si fuera distinto
        final String call = "{ call [core].[sp_RegisterBranch](?, ?, ?, ?) }";
        try (CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, b.getCompanyId());
            cs.setString(2, b.getName().value());
            if (b.getAddress() != null) cs.setString(3, b.getAddress().value()); else cs.setNull(3, Types.NVARCHAR);
            cs.setString(4, b.getUbigeoId().value()); // tratar como NVARCHAR para preservar ceros
            boolean hasRs = cs.execute();

            // Esperamos un resultset con una columna BranchID (por tu SP)
            if (hasRs) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        return rs.getInt("BranchID");
                    }
                }
            }
            // Si el SP retorna como OUT param, adapta aquí (no parece ser tu caso)
            return null;
        }
    }

    private Branch fetchBranchById(Connection con, int branchId) throws SQLException {
        final String sql = """
            SELECT BranchID, CompanyID, Name, Address, UbigeoID, IsActive, CreatedUtc, UpdatedUtc
            FROM [core].[Branch]
            WHERE BranchID = ?
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new ApplicationException("No se encontró la Branch recién creada: id=" + branchId);

                int companyId = rs.getInt("CompanyID");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                String ubigeo = rs.getString("UbigeoID");
                boolean isActive = rs.getBoolean("IsActive");

                // Maneja timestamps que podrían venir como null
                Timestamp createdTs = rs.getTimestamp("CreatedUtc");
                Timestamp updatedTs = rs.getTimestamp("UpdatedUtc");
                OffsetDateTime created = createdTs != null ? createdTs.toInstant().atOffset(ZoneOffset.UTC) : null;
                OffsetDateTime updated = updatedTs != null ? updatedTs.toInstant().atOffset(ZoneOffset.UTC) : null;

                return Branch.fromPersistence(
                        branchId,
                        companyId,
                        new BranchName(name),
                        address != null ? new AddressLine(address) : null,
                        new UbigeoId(ubigeo),
                        isActive,
                        created,
                        updated
                );
            }
        }
    }

    // -------------------- Stubs de OrganizationsQueryGateway (por si el handler activa flags en el futuro)

    @Override
    public boolean existsBranchNameInCompany(int companyId, String branchName) {
        // Implementar cuando actives organizations.rules.enforce-unique-branch-name=true
        return false;
    }

    @Override
    public boolean isCompanyActive(int companyId) {
        // Implementar cuando actives organizations.rules.require-company-active=true
        return true;
    }
}
