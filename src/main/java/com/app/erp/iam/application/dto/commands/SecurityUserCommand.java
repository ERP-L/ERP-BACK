package com.app.erp.iam.application.dto.commands;

public class SecurityUserCommand {
    private String email;
    private String username;     // El login será por email, pero el SP requiere username
    private String rawPassword;  // Se hashea en el handler
    private String encodedPassword;

    public SecurityUserCommand() { }

    public SecurityUserCommand(String email, String username, String rawPassword) {
        this.email = email;
        this.username = username;
        this.rawPassword = rawPassword;
    }

    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getRawPassword() { return rawPassword; }
    public String getEncodedPassword() { return encodedPassword; }
    public void setEncodedPassword(String encodedPassword) { this.encodedPassword = encodedPassword; }
}
