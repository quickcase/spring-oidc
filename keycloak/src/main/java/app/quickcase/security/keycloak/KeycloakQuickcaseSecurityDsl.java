package app.quickcase.security.keycloak;

import app.quickcase.security.QuickcaseSecurityDsl;
import app.quickcase.security.authentication.QuickcaseAuthenticationConverter;
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
