package app.quickcase.security.keycloak;

/**
 * Standard and custom claims used in access tokens issued by KeyCloak.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
public interface KeycloakClaims {
    String SUB = "sub";
    String NAME = "name";
    String EMAIL = "email";
    String APP_ROLES = "app_roles";
    String APP_JURISDICTIONS = "app_jurisdictions";
}
