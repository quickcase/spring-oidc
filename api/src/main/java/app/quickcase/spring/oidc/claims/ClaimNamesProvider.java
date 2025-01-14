package app.quickcase.spring.oidc.claims;

/**
 * Dynamic provider of names for OIDC claims retrieved from /userinfo endpoint.
 *
 * @author Valentin Laurin
 * @since 1.0
 */
public interface ClaimNamesProvider {
    default String sub() {
        return "sub";
    }

    default String name() {
        return "name";
    }

    default String email() {
        return "email";
    }

    String roles();
    String groups();
    String organisations();
    String defaultJurisdiction();
    String defaultCaseType();
    String defaultState();
}
