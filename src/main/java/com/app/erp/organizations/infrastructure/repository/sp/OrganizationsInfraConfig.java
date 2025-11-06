package com.app.erp.organizations.infrastructure.repository.sp;

import com.app.erp.organizations.infrastructure.acl.catalogs.CatalogsAclAdapter;
import com.app.erp.organizations.application.port.CatalogsQueryGateway;
import com.app.erp.organizations.application.port.OrganizationsCommandGateway;
import com.app.erp.organizations.application.port.OrganizationsQueryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class OrganizationsInfraConfig {

    @Bean
    public OrganizationsSpGatewayImpl organizationsSpGateway(DataSource ds) {
        return new OrganizationsSpGatewayImpl(ds);
    }

    @Bean
    public CatalogsQueryGateway catalogsQueryGateway(DataSource ds) {
        return new CatalogsAclAdapter(ds);
    }
}
