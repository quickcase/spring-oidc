package app.quickcase.security;

import app.quickcase.security.cognito.CognitoTokenServices;
import app.quickcase.security.cognito.CognitoAuthoritiesExtractor;
import app.quickcase.security.cognito.CognitoPrincipalExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
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

    private final TokenStore jwkTokenStore;
    private final ResourceServerProperties sso;
    private final OAuth2RestOperations restTemplate;

    public QuickcaseSecurityConfig(TokenStore jwkTokenStore,
                                   ResourceServerProperties sso,
                                   UserInfoRestTemplateFactory restTemplateFactory) {
        this.jwkTokenStore = jwkTokenStore;
        this.sso = sso;
        this.restTemplate = restTemplateFactory.getUserInfoRestTemplate();
    }

    @Bean
    @Primary
    public ResourceServerTokenServices resourceServerTokenServices() {
        return new CognitoTokenServices(defaultTokenServices(), userInfoTokenServices());
    }

    private DefaultTokenServices defaultTokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwkTokenStore);
        return defaultTokenServices;
    }

    private UserInfoTokenServices userInfoTokenServices() {
        UserInfoTokenServices services = new UserInfoTokenServices(
                this.sso.getUserInfoUri(), this.sso.getClientId());
        services.setRestTemplate(this.restTemplate);
        services.setTokenType(this.sso.getTokenType());
        services.setAuthoritiesExtractor(new CognitoAuthoritiesExtractor());
        services.setPrincipalExtractor(new CognitoPrincipalExtractor());
        return services;
    }
}
