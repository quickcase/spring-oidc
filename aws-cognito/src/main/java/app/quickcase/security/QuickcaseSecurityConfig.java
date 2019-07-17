package app.quickcase.security;

import app.quickcase.security.cognito.CognitoPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to import in Spring application to auto-configure Spring Security
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Configuration
public class QuickcaseSecurityConfig {

    @Bean
    public PrincipalExtractor principalExtractor() {
        return new CognitoPrincipalExtractor();
    }
}
