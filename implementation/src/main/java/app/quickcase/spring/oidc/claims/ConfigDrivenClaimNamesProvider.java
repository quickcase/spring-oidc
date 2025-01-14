package app.quickcase.spring.oidc.claims;

import app.quickcase.spring.oidc.OidcConfig;

/**
 * Provide names of OIDC claims either overridden via configuration properties or
 * defaulted to QuickCase standard claim names.
 *
 * @author Valentin Laurin
 * @since 1.0
 */
public class ConfigDrivenClaimNamesProvider implements ClaimNamesProvider {
    private final String prefix;
    private final String sub;
    private final String name;
    private final String email;
    private final String roles;
    private final String groups;
    private final String organisations;
    private final String defaultJurisdiction;
    private final String defaultCaseType;
    private final String defaultState;

    public ConfigDrivenClaimNamesProvider(OidcConfig.Claims claimsConfig) {
        this.prefix = claimsConfig.getPrefix();

        final OidcConfig.ClaimNames names = claimsConfig.getNames();
        this.sub = names.getSub();
        this.name = names.getName();
        this.email = names.getEmail();
        this.roles = names.getRoles();
        this.groups = names.getGroups();
        this.organisations = names.getOrganisations();
        this.defaultJurisdiction = names.getDefaultJurisdiction();
        this.defaultCaseType = names.getDefaultCaseType();
        this.defaultState = names.getDefaultState();
    }

    @Override
    public String sub() {
        return sub;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String roles() {
        return prefix + roles;
    }

    @Override
    public String groups() {
        return prefix + groups;
    }

    @Override
    public String organisations() {
        return prefix + organisations;
    }

    @Override
    public String defaultJurisdiction() {
        return prefix + defaultJurisdiction;
    }

    @Override
    public String defaultCaseType() {
        return prefix + defaultCaseType;
    }

    @Override
    public String defaultState() {
        return prefix + defaultState;
    }
}
