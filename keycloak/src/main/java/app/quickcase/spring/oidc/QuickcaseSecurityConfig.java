package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthenticationConverter;
import app.quickcase.spring.oidc.keycloak.KeycloakQuickcaseSecurityDsl;
import app.quickcase.spring.oidc.keycloak.oidc.KeycloakUserInfoExtractor;
import app.quickcase.spring.oidc.oidc.DefaultUserInfoGateway;
import app.quickcase.spring.oidc.oidc.DefaultUserInfoService;
import app.quickcase.spring.oidc.oidc.UserInfoExtractor;
import app.quickcase.spring.oidc.oidc.UserInfoGateway;
import app.quickcase.spring.oidc.oidc.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration to import in Spring application to auto-configure Spring Security to work with AWS
 * KeyCloak.
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
        return new KeycloakUserInfoExtractor();
    }

    @Bean
    public UserInfoService createUserInfoService(UserInfoGateway gateway, UserInfoExtractor extractor) {
        return new DefaultUserInfoService(gateway, extractor);
    }

    @Bean
    public QuickcaseAuthenticationConverter createAuthenticationConverter(
            UserInfoService userInfoService,
            @Value("${quickcase.security.oidc.profile-scope:profile}") String profileScope
    ) {
        return new QuickcaseAuthenticationConverter(userInfoService, profileScope);
    }

    @Bean
    public QuickcaseSecurityDsl createSecurityDsl(QuickcaseAuthenticationConverter authenticationConverter) {
        return new KeycloakQuickcaseSecurityDsl(authenticationConverter);
    }
}
