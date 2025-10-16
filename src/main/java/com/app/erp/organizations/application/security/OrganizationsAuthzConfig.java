package com.app.erp.organizations.application.security;


import com.app.erp.shared.security.RbacService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrganizationsAuthzProperties.class)
public class OrganizationsAuthzConfig {

    @Bean
    public OrganizationsAuthorizationPolicy organizationsAuthorizationPolicy(
            OrganizationsAuthzProperties props,
            RbacService rbacService
    ) {
        System.out.println("Roles configurados para crear branch: " + props.getAllowedRoles().getCreateBranch());
        return new OrganizationsAuthorizationPolicy(
                props.getAllowedRoles().getCreateBranch(),
                rbacService
        );
    }


}
