package com.app.erp.iam.interfaces.rest.controller;

import com.app.erp.shared.security.SecurityClaimNames;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class MeController {

    @Operation(summary="Info del usuario", security={@SecurityRequirement(name="bearer-jwt")})
    @GetMapping({"/me","/auth/me"})
    public Map<String, Object> me(Authentication auth) {
        if (auth == null) return Map.of("authenticated", false);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("authenticated", auth.isAuthenticated());
        out.put("principal", auth.getName());
        out.put("authClass", auth.getClass().getName());
        out.put("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        if (auth instanceof JwtAuthenticationToken jwt) {
            Map<String, Object> c = jwt.getTokenAttributes();
            out.put("claims", Map.of(
                    SecurityClaimNames.CID, c.get(SecurityClaimNames.CID),
                    SecurityClaimNames.SID, c.get(SecurityClaimNames.SID),
                    SecurityClaimNames.AID, c.get(SecurityClaimNames.AID),
                    SecurityClaimNames.ROLES_COMPANY, c.get(SecurityClaimNames.ROLES_COMPANY),
                    SecurityClaimNames.ROLES_GLOBAL, c.get(SecurityClaimNames.ROLES_GLOBAL)
            ));
            out.put("rolesCompanyParsed", asIntList(c.get(SecurityClaimNames.ROLES_COMPANY)));
            out.put("rolesGlobalParsed", asIntList(c.get(SecurityClaimNames.ROLES_GLOBAL)));
        } else {
            out.put("details", auth.getDetails());
        }
        return out;
    }

    private static List<Integer> asIntList(Object value) {
        if (value == null) return List.of();
        if (value instanceof List<?> list) {
            List<Integer> out = new ArrayList<>();
            for (Object v : list) {
                if (v instanceof Number n) out.add(n.intValue());
                else if (v instanceof String s && !s.isBlank()) out.add(Integer.parseInt(s));
            }
            return out;
        }
        if (value instanceof Number n) return List.of(n.intValue());
        if (value instanceof String s && !s.isBlank()) {
            String[] parts = s.split(",");
            List<Integer> out = new ArrayList<>(parts.length);
            for (String p : parts) if (!p.isBlank()) out.add(Integer.parseInt(p.trim()));
            return out;
        }
        return List.of();
    }
}