package com.app.erp.iam.application.dto.commands;

public class TenantUserCommand {
    private String firstName;
    private String lastName;
    private String gender;          // M, F, O
    private String phone;
    private Integer documentTypeId;
    private String documentNumber;

    public TenantUserCommand() { }

    public TenantUserCommand(String firstName, String lastName, String gender,
                             String phone, Integer documentTypeId, String documentNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.documentTypeId = documentTypeId;
        this.documentNumber = documentNumber;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public Integer getDocumentTypeId() { return documentTypeId; }
    public String getDocumentNumber() { return documentNumber; }
}
