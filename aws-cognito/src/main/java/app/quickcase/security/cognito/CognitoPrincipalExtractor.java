package app.quickcase.security.cognito;

import app.quickcase.security.UserInfo;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Set;

import static app.quickcase.security.cognito.CognitoClaims.APP_JURISDICTIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static app.quickcase.security.cognito.CognitoClaims.EMAIL;
import static app.quickcase.security.cognito.CognitoClaims.NAME;
import static app.quickcase.security.cognito.CognitoClaims.SUB;
import static app.quickcase.security.utils.AuthoritiesUtils.fromCommaSeparated;

public class CognitoPrincipalExtractor implements PrincipalExtractor {
    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        Set<GrantedAuthority> authorities = fromCommaSeparated(String.valueOf(map.get(APP_ROLES)));
        return UserInfo.builder()
                       .id(String.valueOf(map.get(SUB)))
                       .name(String.valueOf(map.get(NAME)))
                       .email(String.valueOf(map.get(EMAIL)))
                       .authorities(authorities)
                       .jurisdictions(String.valueOf(map.get(APP_JURISDICTIONS)).split(","))
                       .build();
    }
}
