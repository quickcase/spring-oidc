package app.quickcase.security.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface AuthoritiesUtils {

    static Set<GrantedAuthority> fromCommaSeparated(String authoritiesStr) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Arrays.stream(authoritiesStr.split(","))
              .map(SimpleGrantedAuthority::new)
              .forEach(authorities::add);
        return authorities;
    }

    static String toCommaSeparated(Set<GrantedAuthority> authorities) {
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .collect(Collectors.joining(","));
    }

}
