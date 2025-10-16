package com.app.erp.iam.interfaces.rest.resources.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "SecurityUserDto", description = "Credenciales globales del usuario")
public class SecurityUserDto {

    @Schema(example = "owner@easyshop.pe")
    @NotBlank @Email @Size(max = 256)
    private String email;

    @Schema(example = "owner_easyshop")
    @NotBlank @Size(min = 4, max = 100)
    private String username;

    @Schema(example = "Str0ngP@ss!", description = "Se enviará en claro; el backend lo hashea")
    @NotBlank @Size(min = 8, max = 200)
    private String password;

    // Getters/Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
