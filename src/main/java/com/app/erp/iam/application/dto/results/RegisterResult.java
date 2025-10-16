package com.app.erp.iam.application.dto.results;

public class RegisterResult {
    private final Integer companyId;
    private final Integer securityUserId;
    private final Integer authUserId;

    public RegisterResult(Integer companyId, Integer securityUserId, Integer authUserId) {
        this.companyId = companyId;
        this.securityUserId = securityUserId;
        this.authUserId = authUserId;
    }

    public Integer getCompanyId() { return companyId; }
    public Integer getSecurityUserId() { return securityUserId; }
    public Integer getAuthUserId() { return authUserId; }
}
