package com.app.erp.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegisterResponse", description = "IDs creados por el SP tras el registro")
public class RegisterResponse {

    @Schema(example = "101")
    private Integer companyId;

    @Schema(example = "2001")
    private Integer securityUserId;

    @Schema(example = "3001")
    private Integer authUserId;

    public RegisterResponse(Integer companyId, Integer securityUserId, Integer authUserId) {
        this.companyId = companyId;
        this.securityUserId = securityUserId;
        this.authUserId = authUserId;
    }

    public Integer getCompanyId() { return companyId; }
    public Integer getSecurityUserId() { return securityUserId; }
    public Integer getAuthUserId() { return authUserId; }
}
