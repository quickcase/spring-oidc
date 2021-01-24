package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthenticationConverter;
import app.quickcase.spring.oidc.claims.ClaimNamesProvider;
import app.quickcase.spring.oidc.claims.ConfigDrivenClaimNamesProvider;
import app.quickcase.spring.oidc.userinfo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration to import in Spring application to auto-configure Spring Security to work with a QuickCase-compliant
 * OIDC provider.
 * This configuration relies on both properties `security.oauth2.resource.jwk.key-set-uri` and
 * `security.oauth2.resource.user-info-uri` being defined in Spring application.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Configuration
public class QuickcaseSecurityConfig {
    @Bean
    public UserInfoGateway createUserInfoGateway(OidcConfig oidcConfig) throws URISyntaxException {
        return new DefaultUserInfoGateway(new URI(oidcConfig.getUserInfoUri()), new RestTemplate());
    }

    @Bean
    public ClaimNamesProvider createClaimNamesProvider(OidcConfig oidcConfig) {
        return new ConfigDrivenClaimNamesProvider(oidcConfig.getClaims());
    }

    @Bean
    public UserInfoExtractor createUserInfoExtractor(ClaimNamesProvider claimNamesProvider) {
        return new DefaultUserInfoExtractor(claimNamesProvider);
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
        return new DefaultQuickcaseSecurityDsl(authenticationConverter);
    }
}
