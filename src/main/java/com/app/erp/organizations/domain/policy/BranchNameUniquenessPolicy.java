package com.app.erp.organizations.domain.policy;

public interface BranchNameUniquenessPolicy {
    boolean isUnique(int companyId, String branchName);
}
