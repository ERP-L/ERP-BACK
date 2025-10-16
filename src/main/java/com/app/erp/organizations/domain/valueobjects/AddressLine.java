package com.app.erp.organizations.domain.valueobjects;


import com.app.erp.organizations.domain.exception.InvalidAddressException;

import java.util.Objects;

public record AddressLine(String value) {
    private static final int MAX = 300;

    public AddressLine {
        Objects.requireNonNull(value, "Address");
        value = value.trim();
        if (value.length() > MAX) throw new InvalidAddressException("Dirección excede " + MAX + " caracteres");
    }

    @Override public String toString() { return value; }

    public static AddressLine ofNullable(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isEmpty()) return null;
        return new AddressLine(t);
    }
}
