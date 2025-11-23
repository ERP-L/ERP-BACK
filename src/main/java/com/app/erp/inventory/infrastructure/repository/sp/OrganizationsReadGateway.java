package com.app.erp.inventory.infrastructure.repository.queries;

import com.app.erp.inventory.application.port.OrganizationsReadPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationsReadGateway implements OrganizationsReadPort {

    private final JdbcTemplate jdbc;

    public OrganizationsReadGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Integer getBranchCompanyId(int branchId) {
        return jdbc.query(
                "SELECT CompanyID FROM core.Branch WHERE BranchID = ?",
                ps -> ps.setInt(1, branchId),
                rs -> rs.next() ? rs.getInt("CompanyID") : null
        );
    }
}
