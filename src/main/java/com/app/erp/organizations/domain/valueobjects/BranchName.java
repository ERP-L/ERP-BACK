package com.app.erp.organizations.domain.valueobjects;

import com.app.erp.organizations.domain.exception.InvalidBranchNameException;

import java.util.Objects;

public record BranchName(String value) {
    private static final int MAX = 200;

    public BranchName {
        Objects.requireNonNull(value, "BranchName");
        value = value.trim();
        if (value.isEmpty()) throw new InvalidBranchNameException("El nombre de la sucursal no puede ser vacío");
        if (value.length() > MAX) throw new InvalidBranchNameException("Nombre excede " + MAX + " caracteres");
    }

    @Override public String toString() { return value; }
}
