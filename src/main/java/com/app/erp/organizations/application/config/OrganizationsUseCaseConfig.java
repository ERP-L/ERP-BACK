package com.app.erp.organizations.application.config;


import com.app.erp.organizations.application.usecase.RegisterBranchHandler;
import com.app.erp.organizations.application.usecase.ListBranchesHandler;
import com.app.erp.organizations.application.port.CatalogsQueryGateway;
import com.app.erp.organizations.application.port.OrganizationsCommandGateway;
import com.app.erp.organizations.application.port.OrganizationsQueryGateway;
import com.app.erp.organizations.application.security.OrganizationsAuthorizationPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrganizationsRulesProperties.class)
public class OrganizationsUseCaseConfig {

    @Bean
    public RegisterBranchHandler registerBranchHandler(OrganizationsAuthorizationPolicy authz,
                                                       OrganizationsCommandGateway commandGateway,
                                                       OrganizationsQueryGateway queryGateway,
                                                       CatalogsQueryGateway catalogsGateway,
                                                       OrganizationsRulesProperties rules) {
        return new RegisterBranchHandler(authz, commandGateway, queryGateway, catalogsGateway, rules);
    }

    @Bean
    public ListBranchesHandler listBranchesHandler(OrganizationsAuthorizationPolicy authz,
                                                   OrganizationsQueryGateway queryGateway) {
        return new ListBranchesHandler(authz, queryGateway);
    }
}
