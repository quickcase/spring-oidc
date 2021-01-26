package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthenticationConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class DefaultQuickcaseSecurityDsl implements QuickcaseSecurityDsl {

    private final OidcConfig oidcConfig;
    private final QuickcaseAuthenticationConverter authenticationConverter;

    public DefaultQuickcaseSecurityDsl(OidcConfig oidcConfig,
                                       QuickcaseAuthenticationConverter authenticationConverter) {
        this.oidcConfig = oidcConfig;
        this.authenticationConverter = authenticationConverter;
    }

    @Override
    public HttpSecurity withQuickcaseSecurity(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer()
            .jwt()
            .jwkSetUri(oidcConfig.getJwkSetUri())
            .jwtAuthenticationConverter(authenticationConverter);
        return http;
    }
}
