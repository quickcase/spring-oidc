package app.quickcase.security.cognito.oidc;

import app.quickcase.security.OrganisationProfile;
import app.quickcase.security.UserInfo;
import app.quickcase.security.UserPreferences;
import app.quickcase.security.oidc.OidcException;
import app.quickcase.security.oidc.UserInfoExtractor;
import app.quickcase.security.utils.ClaimsParser;
import app.quickcase.security.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static app.quickcase.security.cognito.CognitoClaims.APP_JURISDICTIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ORGANISATIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static app.quickcase.security.cognito.CognitoClaims.EMAIL;
import static app.quickcase.security.cognito.CognitoClaims.NAME;
import static app.quickcase.security.cognito.CognitoClaims.SUB;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_STATE;

@Slf4j
public class CognitoUserInfoExtractor implements UserInfoExtractor {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final OrganisationProfileParser ORG_PARSER = new OrganisationProfileParser();

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
        return claimsParser.getString(APP_ORGANISATIONS)
                           .flatMap(json -> {
                               try {
                                   return Optional.of(JSON_MAPPER.readTree(json));
                               } catch (JsonProcessingException e) {
                                   log.warn(
                                           "Unable to parse organisation profiles JSON for user `{}`: {}",
                                           claimsParser.getString(SUB),
                                           json, e);
                                   return Optional.empty();
                               }
                           })
                           .map(ORG_PARSER::parse)
                           .orElse(Collections.emptyMap());
    }
}
