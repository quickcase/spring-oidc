package app.quickcase.security.cognito;

import app.quickcase.security.authentication.QuickcaseAuthenticationConverter;
import app.quickcase.security.QuickcaseSecurityDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class CognitoQuickcaseSecurityDsl implements QuickcaseSecurityDsl {

    private final QuickcaseAuthenticationConverter authenticationConverter;

    public CognitoQuickcaseSecurityDsl(QuickcaseAuthenticationConverter authenticationConverter) {
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
