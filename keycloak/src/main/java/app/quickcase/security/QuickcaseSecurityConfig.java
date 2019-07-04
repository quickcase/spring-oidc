package app.quickcase.security;

import app.quickcase.security.keycloak.KeycloakUserAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Configuration to import in Spring application to auto-configure Spring Security
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Configuration
public class QuickcaseSecurityConfig {

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
        accessTokenConverter.setAccessTokenConverter(customAccessTokenConverter());
        return new JwtTokenStore(accessTokenConverter);
    }

    private DefaultAccessTokenConverter customAccessTokenConverter() {
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        tokenConverter.setUserTokenConverter(new KeycloakUserAuthenticationConverter());
        return tokenConverter;
    }
}
