package app.quickcase.security.keycloak;

import app.quickcase.security.UserAuthenticationToken;
import app.quickcase.security.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static app.quickcase.security.keycloak.KeycloakClaims.*;

/**
 * Convert KeyCloak access token claims to/from Spring's {@link Authentication}.
 *
 * <p>
 * User details are stored using QuickCase's {@link UserInfo} and encapsulated within an
 * authenticated {@link UserAuthenticationToken}.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
public class KeycloakUserAuthenticationConverter implements UserAuthenticationConverter {

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        final HashMap<String, String> claims = new HashMap<>();
        final Object principal = userAuthentication.getPrincipal();
        if (principal instanceof UserInfo) {
            final UserInfo userInfo = (UserInfo) principal;
            claims.put(SUB, userInfo.getId());
            claims.put(EMAIL, userInfo.getEmail());
            claims.put(NAME, userInfo.getName());
            claims.put(APP_ROLES, authoritiesToString(userInfo.getAuthorities()));
        }
        return claims;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        Set<GrantedAuthority> authorities = stringToAuthorities(String.valueOf(map.get(APP_ROLES)));
        UserInfo user = UserInfo.builder()
                                .id(String.valueOf(map.get(SUB)))
                                .name(String.valueOf(map.get(NAME)))
                                .email(String.valueOf(map.get(EMAIL)))
                                .authorities(authorities)
                                .build();
        return new UserAuthenticationToken(user);
    }

    private String authoritiesToString(Set<GrantedAuthority> authorities) {
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .collect(Collectors.joining(","));
    }

    private Set<GrantedAuthority> stringToAuthorities(String authoritiesStr) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Arrays.stream(authoritiesStr.split(","))
              .map(SimpleGrantedAuthority::new)
              .forEach(authorities::add);
        return authorities;
    }
}
