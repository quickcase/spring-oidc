package app.quickcase.spring.oidc.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface StringUtils {

    static Set<GrantedAuthority> fromCommaSeparated(String authoritiesStr) {
        return authorities(fromString(authoritiesStr, ","));
    }

    static String toCommaSeparated(Set<GrantedAuthority> authorities) {
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .collect(Collectors.joining(","));
    }

    static Set<String> fromSpaceSeparated(String authoritiesStr) {
        return fromString(authoritiesStr, " ");
    }

    static Set<GrantedAuthority> authorities(Set<String> authorities) {
        return authorities.stream()
                          .map(SimpleGrantedAuthority::new)
                          .collect(Collectors.toSet());
    }

    static Set<GrantedAuthority> authorities(String... authorities) {
        return Arrays.stream(authorities)
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toSet());
    }

    static Set<String> fromString(String authoritiesStr, String delimiterRegex) {
        return new HashSet<>(Arrays.asList(authoritiesStr.split(delimiterRegex)));
    }
}
