package com.app.erp.iam.interfaces.rest.resources.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "TenantUserDto", description = "Datos personales del usuario dentro de la compañía")
public class TenantUserDto {

    @Schema(example = "Armando")
    @NotBlank @Size(max = 100)
    private String firstName;

    @Schema(example = "Mendoza")
    @NotBlank @Size(max = 100)
    private String lastName;

    @Schema(example = "F", description = "Valores permitidos: M, F, O")
    @NotBlank @Pattern(regexp = "M|F|O")
    private String gender;

    @Schema(example = "999999999")
    @NotBlank @Size(max = 30)
    private String phone;

    @Schema(example = "1", description = "Tipo de documento de la persona")
    @NotNull
    private Integer documentTypeId;

    @Schema(example = "12345678")
    @NotBlank @Size(max = 32)
    private String documentNumber;

    // Getters/Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getDocumentTypeId() { return documentTypeId; }
    public void setDocumentTypeId(Integer documentTypeId) { this.documentTypeId = documentTypeId; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
}
