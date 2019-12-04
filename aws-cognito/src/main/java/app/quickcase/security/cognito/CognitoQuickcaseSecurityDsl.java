package app.quickcase.security.cognito;

import app.quickcase.security.QuickcaseSecurityDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class CognitoQuickcaseSecurityDsl implements QuickcaseSecurityDsl {

    private final CognitoAuthenticationConverter authenticationConverter;

    public CognitoQuickcaseSecurityDsl(CognitoAuthenticationConverter authenticationConverter) {
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
