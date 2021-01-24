package app.quickcase.spring.oidc;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Consolidated configuration of all properties under `quickcase.oidc` namespace.
 * All optional configuration properties have default values provided to reduce null checks.
 *
 * @author Valentin Laurin
 * @since 1.0
 */
@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "quickcase.oidc")
public class OidcConfig {
    private final String userInfoUri;
}
