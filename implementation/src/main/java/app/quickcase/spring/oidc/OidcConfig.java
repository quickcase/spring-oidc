package app.quickcase.spring.oidc;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import static app.quickcase.spring.oidc.claims.DefaultClaims.*;

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
    private final Claims claims;

    public OidcConfig(String userInfoUri, @DefaultValue Claims claims) {
        this.userInfoUri = userInfoUri;
        this.claims = claims;
    }

    @Value
    public static class Claims {
        private final ClaimNames names;

        public Claims(@DefaultValue ClaimNames names) {
            this.names = names;
        }
    }

    @Value
    public static class ClaimNames {
        private final String sub;
        private final String name;
        private final String email;
        private final String roles;
        private final String organisations;
        private final String defaultJurisdiction;
        private final String defaultCaseType;
        private final String defaultState;

        public ClaimNames(@DefaultValue(SUB) String sub,
                          @DefaultValue(NAME) String name,
                          @DefaultValue(EMAIL) String email,
                          @DefaultValue(APP_ROLES) String roles,
                          @DefaultValue(APP_ORGANISATIONS) String organisations,
                          @DefaultValue(USER_DEFAULT_JURISDICTION) String defaultJurisdiction,
                          @DefaultValue(USER_DEFAULT_CASE_TYPE) String defaultCaseType,
                          @DefaultValue(USER_DEFAULT_STATE) String defaultState) {
            this.sub = sub;
            this.name = name;
            this.email = email;
            this.roles = roles;
            this.organisations = organisations;
            this.defaultJurisdiction = defaultJurisdiction;
            this.defaultCaseType = defaultCaseType;
            this.defaultState = defaultState;
        }
    }
}
