package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.OidcException;
import app.quickcase.spring.oidc.claims.ClaimNamesProvider;
import app.quickcase.spring.oidc.organisation.JsonOrganisationProfilesParser;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.utils.ClaimsParser;
import app.quickcase.spring.oidc.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class DefaultUserInfoExtractor implements UserInfoExtractor {
    private static final JsonOrganisationProfilesParser ORG_PARSER = new JsonOrganisationProfilesParser();

    private final ClaimNamesProvider claimNames;

    public DefaultUserInfoExtractor(ClaimNamesProvider claimNamesProvider) {
        this.claimNames = claimNamesProvider;
    }

    @Override
    public UserInfo extract(Map<String, JsonNode> claims) {
        final ClaimsParser claimsParser = new ClaimsParser(claims);
        final UserInfo.UserInfoBuilder builder = UserInfo.builder();

        claimsParser.getString(claimNames.sub())
                    .ifPresentOrElse(builder::id, () -> {
                        throw new OidcException("Mandatory 'sub' claim missing");
                    });

        claimsParser.getString(claimNames.email())
                    .ifPresentOrElse(builder::email, () -> {
                        throw new OidcException("Mandatory 'email' claim missing");
                    });
        claimsParser.getString(claimNames.name()).ifPresent(builder::name);

        claimsParser.getString(claimNames.roles())
                    .map(StringUtils::fromCommaSeparated)
                    .ifPresent(builder::authorities);

        return builder.preferences(extractPreferences(claimsParser))
                      .organisationProfiles(extractProfiles(claimsParser))
                      .build();
    }

    private UserPreferences extractPreferences(ClaimsParser claimsParser) {
        final UserPreferences.UserPreferencesBuilder builder = UserPreferences.builder();

        claimsParser.getString(claimNames.defaultJurisdiction()).ifPresent(builder::defaultJurisdiction);
        claimsParser.getString(claimNames.defaultCaseType()).ifPresent(builder::defaultCaseType);
        claimsParser.getString(claimNames.defaultState()).ifPresent(builder::defaultState);

        return builder.build();
    }

    private Map<String, OrganisationProfile> extractProfiles(ClaimsParser claimsParser) {
        return claimsParser.getNode(claimNames.organisations())
                           .map(ORG_PARSER::parse)
                           .orElse(Collections.emptyMap());
    }
}
