package app.quickcase.spring.oidc.userinfo;

import java.util.Collections;
import java.util.Map;

import app.quickcase.spring.oidc.OidcException;
import app.quickcase.spring.oidc.claims.ClaimNamesProvider;
import app.quickcase.spring.oidc.claims.ClaimsParser;
import app.quickcase.spring.oidc.organisation.JsonOrganisationProfilesParser;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultUserInfoExtractor implements UserInfoExtractor {
    private static final JsonOrganisationProfilesParser ORG_PARSER = new JsonOrganisationProfilesParser();

    private final ClaimNamesProvider claimNames;

    public DefaultUserInfoExtractor(ClaimNamesProvider claimNamesProvider) {
        this.claimNames = claimNamesProvider;
    }

    @Override
    public UserInfo extract(ClaimsParser claimsParser) {
        final String subject = claimsParser.getString(claimNames.sub())
                                           .orElseThrow(() -> new OidcException("Mandatory subject claim missing: " + claimNames.sub()));

        final UserInfo.UserInfoBuilder builder = UserInfo.builder(subject);

        claimsParser.getString(claimNames.name()).ifPresent(builder::name);
        claimsParser.getString(claimNames.email()).ifPresent(builder::email);

        claimsParser.getString(claimNames.roles())
                    .map(StringUtils::fromCommaSeparated)
                    .ifPresent(builder::authorities);

        claimsParser.getString(claimNames.roles())
                    .map((str) -> StringUtils.fromString(str, ","))
                    .ifPresent(builder::roles);

        claimsParser.getString(claimNames.groups())
                    .map((str) -> StringUtils.fromString(str, ","))
                    .ifPresent(builder::groups);

        return builder.preferences(extractPreferences(claimsParser))
                      .organisationProfiles(extractProfiles(subject, claimsParser))
                      .build();
    }

    private UserPreferences extractPreferences(ClaimsParser claimsParser) {
        final UserPreferences.UserPreferencesBuilder builder = UserPreferences.builder();

        claimsParser.getString(claimNames.defaultJurisdiction()).ifPresent(builder::defaultJurisdiction);
        claimsParser.getString(claimNames.defaultCaseType()).ifPresent(builder::defaultCaseType);
        claimsParser.getString(claimNames.defaultState()).ifPresent(builder::defaultState);

        return builder.build();
    }

    private Map<String, OrganisationProfile> extractProfiles(String subject, ClaimsParser claimsParser) {
        log.debug("Extracting organisation profiles for subject `{}`", subject);
        return claimsParser.getObject(claimNames.organisations())
                           .map(ORG_PARSER::parse)
                           .orElseGet(() -> {
                               log.warn("No organisation profiles extracted for subject `{}`", subject);
                               return Collections.emptyMap();
                           });
    }
}
