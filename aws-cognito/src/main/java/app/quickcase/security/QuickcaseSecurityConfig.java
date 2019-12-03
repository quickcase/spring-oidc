package app.quickcase.security;

import app.quickcase.security.cognito.CognitoTokenServices;
import app.quickcase.security.cognito.CognitoAuthoritiesExtractor;
import app.quickcase.security.cognito.CognitoPrincipalExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

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
    @Primary
    @Autowired
    @ConditionalOnProperty(prefix = "quickcase.security.aws-cognito", name = "enable-machine", havingValue = "true")
    public ResourceServerTokenServices resourceServerTokenServices(
            TokenStore jwkTokenStore,
            ResourceServerProperties sso,
            UserInfoRestTemplateFactory restTemplateFactory) {
        return new CognitoTokenServices(defaultTokenServices(jwkTokenStore),
                                        userInfoTokenServices(sso, restTemplateFactory));
    }

    @Bean
    public PrincipalExtractor principalExtractor() {
        return new CognitoPrincipalExtractor();
    }

    @Bean
    public AuthoritiesExtractor authoritiesExtractor() {
        return new CognitoAuthoritiesExtractor();
    }

    private DefaultTokenServices defaultTokenServices(TokenStore jwkTokenStore) {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwkTokenStore);
        return defaultTokenServices;
    }

    private UserInfoTokenServices userInfoTokenServices(ResourceServerProperties sso,
                                                        UserInfoRestTemplateFactory restTemplateFactory) {
        UserInfoTokenServices services = new UserInfoTokenServices(sso.getUserInfoUri(),
                                                                   sso.getClientId());
        services.setRestTemplate(restTemplateFactory.getUserInfoRestTemplate());
        services.setTokenType(sso.getTokenType());
        services.setAuthoritiesExtractor(authoritiesExtractor());
        services.setPrincipalExtractor(principalExtractor());
        return services;
    }
}
