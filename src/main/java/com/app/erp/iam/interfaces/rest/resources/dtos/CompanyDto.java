package com.app.erp.iam.interfaces.rest.resources.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "CompanyDto", description = "Datos de la compañía a crear")
public class CompanyDto {

    @Schema(example = "EASYSHOP S.A.C.")
    @NotBlank @Size(max = 200)
    private String legalName;

    @Schema(description = "Debe ser 3", example = "3")
    @NotNull
    private Integer documentTypeId;

    @Schema(example = "20609988777")
    @NotBlank @Size(max = 32)
    private String documentNumber;

    @Schema(example = "EASYSHOP")
    @NotBlank @Size(max = 200)
    private String tradeName;

    @Schema(example = "Av. Los Olivos 123, Lima")
    @NotBlank @Size(max = 200)
    private String address;

    @Schema(example = "150101", description = "Ubigeo RENIEC")
    @NotNull
    private Integer ubigeoId;

    @Schema(example = "999999999")
    @NotBlank @Size(max = 30)
    private String phone;

    @Schema(example = "contacto@easyshop.pe")
    @NotBlank @Email @Size(max = 256)
    private String email;

    // Getters/Setters
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public Integer getDocumentTypeId() { return documentTypeId; }
    public void setDocumentTypeId(Integer documentTypeId) { this.documentTypeId = documentTypeId; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getUbigeoId() { return ubigeoId; }
    public void setUbigeoId(Integer ubigeoId) { this.ubigeoId = ubigeoId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
