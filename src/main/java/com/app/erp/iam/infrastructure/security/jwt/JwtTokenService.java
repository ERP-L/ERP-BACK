package com.app.erp.iam.infrastructure.security.jwt;

import com.app.erp.iam.application.internal.outboundservices.tokens.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenService implements TokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer:iam-service}") String issuer,
            @Value("${security.jwt.expiration-seconds:1800}") long expirationSeconds
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    @Override
    public String generateToken(String username) {
        return generateToken(username, Map.of());
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        var builder = JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expirationSeconds)));

        if (claims != null) {
            claims.forEach((k, v) -> {
                if (v == null) return;

                if (v instanceof Boolean b) {
                    builder.withClaim(k, b);
                } else if (v instanceof Integer i) {
                    builder.withClaim(k, i);
                } else if (v instanceof Long l) {
                    builder.withClaim(k, l);
                } else if (v instanceof String s) {
                    builder.withClaim(k, s);
                } else if (v instanceof java.util.List<?> list) {
                    // Listas comunes: List<Integer>/List<String>/List<Long>/List<Boolean>
                    if (list.isEmpty()) {
                        builder.withClaim(k, java.util.Collections.<Integer>emptyList());
                        return;
                    }
                    Object first = list.get(0);
                    if (first instanceof Integer) {
                        builder.withClaim(k, (java.util.List<Integer>) list);
                    } else if (first instanceof String) {
                        builder.withClaim(k, (java.util.List<String>) list);
                    } else if (first instanceof Long) {
                        builder.withClaim(k, (java.util.List<Long>) list);
                    } else if (first instanceof Boolean) {
                        builder.withClaim(k, (java.util.List<Boolean>) list);
                    } else {
                        builder.withClaim(k, list.toString()); // fallback seguro
                    }
                } else if (v instanceof Integer[] arrI) {
                    builder.withArrayClaim(k, arrI);
                } else if (v instanceof String[] arrS) {
                    builder.withArrayClaim(k, arrS);
                } else if (v instanceof Long[] arrL) {
                    builder.withClaim(k, java.util.Arrays.asList(arrL)); // compat
                } else if (v instanceof Boolean[] arrB) {
                    builder.withClaim(k, java.util.Arrays.asList(arrB));
                } else {
                    builder.withClaim(k, v.toString()); // último recurso
                }
            });
        }

        return builder.sign(algorithm);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return verifier.verify(token).getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
