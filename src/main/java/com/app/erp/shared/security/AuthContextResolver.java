package com.app.erp.shared.security;


import com.app.erp.shared.claims.ClaimsUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AuthContextResolver {

    /** Devuelve un AuthContext “seguro” incluso si los claims vienen con tipos mixtos. */
    public AuthContext resolve(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return new AuthContext(0, null, Set.of(), Set.of());
        }
        Jwt jwt = jwtAuth.getToken();

        Integer userId     = ClaimsUtil.asInteger(jwt.getClaim(SecurityClaimNames.SID));
        Integer companyId  = ClaimsUtil.asInteger(jwt.getClaim(SecurityClaimNames.CID));
        Set<Integer> rc    = ClaimsUtil.asIntegerSet(jwt.getClaim(SecurityClaimNames.ROLES_COMPANY));
        Set<Integer> rg    = ClaimsUtil.asIntegerSet(jwt.getClaim(SecurityClaimNames.ROLES_GLOBAL));

        return new AuthContext(userId != null ? userId : 0, companyId, rc, rg);
    }
}
