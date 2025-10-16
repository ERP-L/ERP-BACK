package com.app.erp.iam.infrastructure.crypto;

import com.app.erp.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class SqlPasswordHasher implements HashingService {

    @Value("${security.password.algorithm:SHA-256}")
    private String algorithm;

    @Value("${security.password.output:hex-upper}") // hex-upper | hex-lower | base64
    private String outputFormat;

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
            return switch (outputFormat) {
                case "hex-lower" -> HexFormat.of().formatHex(digest);
                case "base64"    -> java.util.Base64.getEncoder().encodeToString(digest);
                default          -> HexFormat.of().withUpperCase().formatHex(digest); // hex-upper
            };
        } catch (Exception e) {
            throw new IllegalStateException("Hashing error", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Tu BD compara por igualdad; esto es por compatibilidad
        return encode(rawPassword).equals(encodedPassword);
    }
}
