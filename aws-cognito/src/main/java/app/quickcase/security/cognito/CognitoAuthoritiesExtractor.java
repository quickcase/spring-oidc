package app.quickcase.security.cognito;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static app.quickcase.security.utils.AuthoritiesUtils.fromCommaSeparated;

public class CognitoAuthoritiesExtractor implements AuthoritiesExtractor {

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> claims) {
        return new ArrayList<>(fromCommaSeparated(String.valueOf(claims.get(APP_ROLES))));
    }
}
