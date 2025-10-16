package com.app.erp.organizations.application.dtos.commands;
public record RegisterBranchCommand(
        int companyId,
        String name,
        String address,
        String ubigeoId
) {}
