package app.quickcase.spring.oidc.authentication.converter;

import java.util.Set;
import java.util.stream.Stream;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthentication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import static java.util.stream.Collectors.toSet;

public interface QuickcaseAuthenticationConverter extends Converter<Jwt, QuickcaseAuthentication> {
    static String prefixScope(String scope) {
        return "SCOPE_" + scope;
    }

    static String prefixRole(String role) {
        return "ROLE_" + role;
    }

    static Set<GrantedAuthority> authorities(Set<String> scopes) {
        return authorities(scopes, Set.of());
    }

    static Set<GrantedAuthority> authorities(Set<String> scopes, Set<String> roles) {
        return Stream.concat(
                             scopes.stream().map(QuickcaseAuthenticationConverter::prefixScope),
                             roles.stream().map(QuickcaseAuthenticationConverter::prefixRole)
                     )
                     .map(SimpleGrantedAuthority::new)
                     .collect(toSet());
    }
}
