package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthenticationConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class DefaultQuickcaseSecurityDsl implements QuickcaseSecurityDsl {

    private final QuickcaseAuthenticationConverter authenticationConverter;

    public DefaultQuickcaseSecurityDsl(QuickcaseAuthenticationConverter authenticationConverter) {
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
