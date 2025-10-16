package com.app.erp.iam.application.dto.commands;

public class CompanyCommand {
    private String legalName;
    private Integer documentTypeId; // Debe ser 3 (se validará en el handler)
    private String documentNumber;
    private String tradeName;
    private String address;
    private Integer ubigeoId;
    private String phone;
    private String email;

    public CompanyCommand() { }

    public CompanyCommand(String legalName, Integer documentTypeId, String documentNumber,
                          String tradeName, String address, Integer ubigeoId,
                          String phone, String email) {
        this.legalName = legalName;
        this.documentTypeId = documentTypeId;
        this.documentNumber = documentNumber;
        this.tradeName = tradeName;
        this.address = address;
        this.ubigeoId = ubigeoId;
        this.phone = phone;
        this.email = email;
    }

    public String getLegalName() { return legalName; }
    public Integer getDocumentTypeId() { return documentTypeId; }
    public String getDocumentNumber() { return documentNumber; }
    public String getTradeName() { return tradeName; }
    public String getAddress() { return address; }
    public Integer getUbigeoId() { return ubigeoId; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}
