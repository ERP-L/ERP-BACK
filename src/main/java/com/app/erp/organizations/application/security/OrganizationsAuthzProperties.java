package com.app.erp.organizations.application.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "organizations.authz")
public class OrganizationsAuthzProperties {

    private AllowedRoles allowedRoles = new AllowedRoles();

    public AllowedRoles getAllowedRoles() { return allowedRoles; }
    public void setAllowedRoles(AllowedRoles allowedRoles) { this.allowedRoles = allowedRoles; }

    public static class AllowedRoles {
        private Set<Integer> createBranch = new LinkedHashSet<>();

        public Set<Integer> getCreateBranch() { return createBranch; }
        public void setCreateBranch(Set<Integer> createBranch) { this.createBranch = createBranch; }
    }
}
