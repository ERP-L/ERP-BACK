package com.app.erp.organizations.domain.valueobjects;

import com.app.erp.organizations.domain.exception.InvalidUbigeoFormatException;

import java.util.Objects;
import java.util.regex.Pattern;

public record UbigeoId(String value) {
    private static final Pattern SIX_DIGITS = Pattern.compile("^\\d{6}$");

    public UbigeoId {
        Objects.requireNonNull(value, "UbigeoId");
        value = value.trim();
        if (!SIX_DIGITS.matcher(value).matches()) {
            throw new InvalidUbigeoFormatException("UbigeoId inválido (debe ser 6 dígitos): " + value);
        }
    }

    public String departmentCode() { return value.substring(0, 2); }
    public String provinceCode()   { return value.substring(2, 4); }
    public String districtCode()   { return value.substring(4, 6); }
}
