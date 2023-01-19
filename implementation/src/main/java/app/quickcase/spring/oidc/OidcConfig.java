package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.authentication.converter.UserInfoAuthenticationConverter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import static app.quickcase.spring.oidc.OidcConfigDefault.Claims.*;
import static app.quickcase.spring.oidc.OidcConfigDefault.PREFIX;

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
    private final String jwkSetUri;
    private final String userInfoUri;
    private final String openidScope;
    private final Claims claims;

    public OidcConfig(
            String jwkSetUri,
            String userInfoUri,
            @DefaultValue(UserInfoAuthenticationConverter.OPENID_SCOPE) String openidScope,
            @DefaultValue Claims claims
    ) {
        this.jwkSetUri = jwkSetUri;
        this.userInfoUri = userInfoUri;
        this.openidScope = openidScope;
        this.claims = claims;
    }

    @Value
    public static class Claims {
        /**
         * Prefix applied to all private claims.
         */
        private final String prefix;

        /**
         * Names of all claims, standard and private, used by QuickCase.
         */
        private final ClaimNames names;

        public Claims(@DefaultValue(PREFIX) String prefix,
                      @DefaultValue ClaimNames names) {
            this.prefix = prefix;
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
                          @DefaultValue(QC_ROLES) String roles,
                          @DefaultValue(QC_ORGANISATIONS) String organisations,
                          @DefaultValue(QC_USER_DEFAULT_JURISDICTION) String defaultJurisdiction,
                          @DefaultValue(QC_USER_DEFAULT_CASE_TYPE) String defaultCaseType,
                          @DefaultValue(QC_USER_DEFAULT_STATE) String defaultState) {
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
