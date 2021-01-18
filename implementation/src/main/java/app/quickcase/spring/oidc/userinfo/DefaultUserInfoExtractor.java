package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.OidcException;
import app.quickcase.spring.oidc.organisation.JsonOrganisationProfilesParser;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.utils.ClaimsParser;
import app.quickcase.spring.oidc.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

import static app.quickcase.spring.oidc.DefaultClaims.APP_JURISDICTIONS;
import static app.quickcase.spring.oidc.DefaultClaims.APP_ORGANISATIONS;
import static app.quickcase.spring.oidc.DefaultClaims.APP_ROLES;
import static app.quickcase.spring.oidc.DefaultClaims.EMAIL;
import static app.quickcase.spring.oidc.DefaultClaims.NAME;
import static app.quickcase.spring.oidc.DefaultClaims.SUB;
import static app.quickcase.spring.oidc.DefaultClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.spring.oidc.DefaultClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.spring.oidc.DefaultClaims.USER_DEFAULT_STATE;

@Slf4j
public class DefaultUserInfoExtractor implements UserInfoExtractor {
    private static final JsonOrganisationProfilesParser ORG_PARSER = new JsonOrganisationProfilesParser();

    @Override
    public UserInfo extract(Map<String, JsonNode> claims) {
        final ClaimsParser claimsParser = new ClaimsParser(claims);
        final UserInfo.UserInfoBuilder builder = UserInfo.builder();

        claimsParser.getString(SUB)
                    .ifPresentOrElse(builder::id, () -> {
                        throw new OidcException("Mandatory 'sub' claim missing");
                    });

        claimsParser.getString(EMAIL)
                    .ifPresentOrElse(builder::email, () -> {
                        throw new OidcException("Mandatory 'email' claim missing");
                    });
        claimsParser.getString(NAME).ifPresent(builder::name);

        claimsParser.getString(APP_ROLES)
                    .map(StringUtils::fromCommaSeparated)
                    .ifPresent(builder::authorities);

        claimsParser.getString(APP_JURISDICTIONS)
                    .map(string -> string.split(","))
                    .ifPresent(builder::jurisdictions);

        return builder.preferences(extractPreferences(claimsParser))
                      .organisationProfiles(extractProfiles(claimsParser))
                      .build();
    }

    private UserPreferences extractPreferences(ClaimsParser claimsParser) {
        final UserPreferences.UserPreferencesBuilder builder = UserPreferences.builder();

        claimsParser.getString(USER_DEFAULT_JURISDICTION).ifPresent(builder::defaultJurisdiction);
        claimsParser.getString(USER_DEFAULT_CASE_TYPE).ifPresent(builder::defaultCaseType);
        claimsParser.getString(USER_DEFAULT_STATE).ifPresent(builder::defaultState);

        return builder.build();
    }

    private Map<String, OrganisationProfile> extractProfiles(ClaimsParser claimsParser) {
        return claimsParser.getNode(APP_ORGANISATIONS)
                           .map(ORG_PARSER::parse)
                           .orElse(Collections.emptyMap());
    }
}
