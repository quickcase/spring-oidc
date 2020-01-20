package app.quickcase.security;

import app.quickcase.security.authentication.QuickcaseAuthenticationConverter;
import app.quickcase.security.cognito.CognitoQuickcaseSecurityDsl;
import app.quickcase.security.cognito.oidc.CognitoUserInfoExtractor;
import app.quickcase.security.oidc.DefaultUserInfoGateway;
import app.quickcase.security.oidc.DefaultUserInfoService;
import app.quickcase.security.oidc.UserInfoExtractor;
import app.quickcase.security.oidc.UserInfoGateway;
import app.quickcase.security.oidc.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration to import in Spring application to auto-configure Spring Security to work with AWS
 * Cognito.
 * This configuration relies on both properties `security.oauth2.resource.jwk.key-set-uri` and
 * `security.oauth2.resource.user-info-uri` being defined in Spring application.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Configuration
public class QuickcaseSecurityConfig {
    @Bean
    @ConditionalOnProperty("quickcase.security.oidc.user-info-uri")
    public UserInfoGateway createUserInfoGateway(@Value("${quickcase.security.oidc.user-info-uri}") String userInfoUri) throws URISyntaxException {
        return new DefaultUserInfoGateway(new URI(userInfoUri), new RestTemplate());
    }

    @Bean
    public UserInfoExtractor createUserInfoExtractor() {
        return new CognitoUserInfoExtractor();
    }

    @Bean
    public UserInfoService createUserInfoService(UserInfoGateway gateway, UserInfoExtractor extractor) {
        return new DefaultUserInfoService(gateway, extractor);
    }

    @Bean
    public QuickcaseAuthenticationConverter createAuthenticationConverter(UserInfoService userInfoService) {
        return new QuickcaseAuthenticationConverter(userInfoService);
    }

    @Bean
    public QuickcaseSecurityDsl createSecurityDsl(QuickcaseAuthenticationConverter authenticationConverter) {
        return new CognitoQuickcaseSecurityDsl(authenticationConverter);
    }
}
