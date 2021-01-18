package app.quickcase.spring.oidc.keycloak;

import app.quickcase.spring.oidc.QuickcaseSecurityDsl;
import app.quickcase.spring.oidc.authentication.QuickcaseAuthenticationConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class KeycloakQuickcaseSecurityDsl implements QuickcaseSecurityDsl {

    private final QuickcaseAuthenticationConverter authenticationConverter;

    public KeycloakQuickcaseSecurityDsl(QuickcaseAuthenticationConverter authenticationConverter) {
        this.authenticationConverter = authenticationConverter;
    }

    @Override
    public HttpSecurity withQuickcaseSecurity(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(authenticationConverter);
        return http;
    }
}
