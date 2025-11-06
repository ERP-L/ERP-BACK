package com.app.erp.organizations.infrastructure.repository.sp;
import com.app.erp.organizations.application.port.OrganizationsCommandGateway;
import com.app.erp.organizations.application.port.OrganizationsQueryGateway;
import com.app.erp.organizations.domain.Branch;
import com.app.erp.organizations.infrastructure.error.DbErrorTranslator;
import com.app.erp.organizations.infrastructure.mapper.BranchDbMapper;
import com.app.erp.shared.exceptions.ApplicationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

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
            throw DbErrorTranslator.translate(ex);
        }
    }

    private Integer callSpRegisterBranch(Connection con, Branch b) throws SQLException {
        try (CallableStatement cs = con.prepareCall(SpRegisterBranch.CALL)) {
            cs.setInt(SpRegisterBranch.IDX_COMPANY_ID, b.getCompanyId());
            cs.setString(SpRegisterBranch.IDX_NAME, b.getName().value());
            if (b.getAddress() != null) cs.setString(SpRegisterBranch.IDX_ADDRESS, b.getAddress().value()); else cs.setNull(SpRegisterBranch.IDX_ADDRESS, Types.NVARCHAR);
            cs.setString(SpRegisterBranch.IDX_UBIGEO, b.getUbigeoId().value()); // NVARCHAR para preservar ceros
            boolean hasRs = cs.execute();

            // Esperamos un resultset con una columna BranchID (por tu SP)
            if (hasRs) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        return rs.getInt(SpRegisterBranch.COL_BRANCH_ID);
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
                return BranchDbMapper.toDomainBranch(rs);
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

    @Override
    public List<Branch> listBranchesByCompany(int companyId, Boolean onlyActive) {
        try (Connection con = dataSource.getConnection()) {
            return callSpListByCompany(con, companyId, onlyActive);
        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        }
    }

    private List<Branch> callSpListByCompany(Connection con, int companyId, Boolean onlyActive) throws SQLException {
        try (CallableStatement cs = con.prepareCall(SpListBranchesByCompany.CALL)) {
            cs.setInt(SpListBranchesByCompany.IDX_COMPANY_ID, companyId);
            if (onlyActive == null) cs.setNull(SpListBranchesByCompany.IDX_ONLY_ACTIVE, Types.BIT);
            else cs.setBoolean(SpListBranchesByCompany.IDX_ONLY_ACTIVE, onlyActive);

            boolean hasRs = cs.execute();
            List<Branch> out = new ArrayList<>();
            if (hasRs) {
                try (ResultSet rs = cs.getResultSet()) {
                    while (rs.next()) {
                        out.add(BranchDbMapper.toDomainBranch(rs));
                    }
                }
            }
            return out;
        }
    }

    // -------------------- SP Contracts (inner classes) --------------------

    /** Contrato del SP de registro de sucursal. Ajusta nombres/índices si tu SP difiere. */
    private static final class SpRegisterBranch {
        static final String CALL = "{ call [core].[sp_RegisterBranch](?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_NAME       = 2;
        static final int IDX_ADDRESS    = 3;
        static final int IDX_UBIGEO     = 4;
        static final String COL_BRANCH_ID = "BranchID";
        // Códigos de error de negocio conocidos (ejemplo/hook)
        // static final int ERR_DUPLICATE = 51001;
    }

    /** Contrato del SP de listado por compañía. */
    private static final class SpListBranchesByCompany {
        static final String CALL = "{ call [core].[usp_Branch_ListByCompany](?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_ONLY_ACTIVE = 2;
    }
}
