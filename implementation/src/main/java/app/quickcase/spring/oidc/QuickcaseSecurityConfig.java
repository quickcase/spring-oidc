package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.authentication.converter.AccessTokenAuthenticationConverter;
import app.quickcase.spring.oidc.authentication.converter.QuickcaseAuthenticationConverter;
import app.quickcase.spring.oidc.authentication.converter.UserInfoAuthenticationConverter;
import app.quickcase.spring.oidc.claims.ClaimNamesProvider;
import app.quickcase.spring.oidc.claims.ConfigDrivenClaimNamesProvider;
import app.quickcase.spring.oidc.userinfo.*;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration to import in Spring application to auto-configure Spring Security to work with a QuickCase-compliant
 * OIDC providers.
 * This configuration relies on both properties `quickcase.oidc.jwk-set-uri` and `quickcase.oidc.user-info-uri`
 * being defined as properties in the Spring application.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Configuration
@EnableConfigurationProperties(OidcConfig.class)
public class QuickcaseSecurityConfig {

    @Bean
    public ClaimNamesProvider createClaimNamesProvider(OidcConfig oidcConfig) {
        return new ConfigDrivenClaimNamesProvider(oidcConfig.getClaims());
    }

    @Bean
    public UserInfoExtractor createUserInfoExtractor(ClaimNamesProvider claimNamesProvider) {
        return new DefaultUserInfoExtractor(claimNamesProvider);
    }

    @Bean
    @ConditionalOnProperty(prefix = "quickcase.oidc", name = "mode", havingValue = "user-info", matchIfMissing = true)
    public UserInfoGateway createUserInfoGateway(OidcConfig oidcConfig) throws URISyntaxException {
        return new DefaultUserInfoGateway(new URI(oidcConfig.getUserInfoUri()), new RestTemplate());
    }

    @Bean
    @ConditionalOnProperty(prefix = "quickcase.oidc", name = "mode", havingValue = "user-info", matchIfMissing = true)
    public UserInfoService createUserInfoService(UserInfoGateway gateway, UserInfoExtractor extractor) {
        return new DefaultUserInfoService(gateway, extractor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "quickcase.oidc", name = "mode", havingValue = "user-info", matchIfMissing = true)
    public UserInfoAuthenticationConverter createUserInfoAuthenticationConverter(
            UserInfoService userInfoService,
            OidcConfig oidcConfig
    ) {
        return new UserInfoAuthenticationConverter(userInfoService, oidcConfig.getOpenidScope());
    }

    @Bean
    @ConditionalOnProperty(prefix = "quickcase.oidc", name = "mode", havingValue = "jwt-access-token")
    public AccessTokenAuthenticationConverter createAccessTokenAuthenticationConverter(
            UserInfoExtractor userInfoExtractor,
            OidcConfig oidcConfig
    ) {
        return new AccessTokenAuthenticationConverter(userInfoExtractor, oidcConfig.getOpenidScope());
    }

    @Bean
    public QuickcaseSecurityDsl createSecurityDsl(OidcConfig oidcConfig,
                                                  QuickcaseAuthenticationConverter authenticationConverter) {
        return new DefaultQuickcaseSecurityDsl(oidcConfig, authenticationConverter);
    }
}
