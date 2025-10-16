package com.app.erp.organizations.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBranchRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 300) String address,
        @NotBlank @Pattern(regexp = "^\\d{6}$", message = "ubigeoId debe tener 6 dígitos")
        String ubigeoId
) {}
