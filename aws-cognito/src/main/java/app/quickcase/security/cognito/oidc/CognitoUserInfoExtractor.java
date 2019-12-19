package app.quickcase.security.cognito.oidc;

import app.quickcase.security.OrganisationProfile;
import app.quickcase.security.UserInfo;
import app.quickcase.security.UserPreferences;
import app.quickcase.security.oidc.UserInfoExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static app.quickcase.security.cognito.CognitoClaims.APP_JURISDICTIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ORGANISATIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static app.quickcase.security.cognito.CognitoClaims.EMAIL;
import static app.quickcase.security.cognito.CognitoClaims.NAME;
import static app.quickcase.security.cognito.CognitoClaims.SUB;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_STATE;
import static app.quickcase.security.utils.StringUtils.fromCommaSeparated;

@Slf4j
public class CognitoUserInfoExtractor implements UserInfoExtractor {
    private static final OrganisationProfileParser ORG_PARSER = new OrganisationProfileParser();

    @Override
    public UserInfo extract(Map<String, JsonNode> claims) {
        return extractUserInfo(claims);
    }

    public UserInfo extractUserInfo(Map<String, JsonNode> claims) {
        Set<GrantedAuthority> authorities = fromCommaSeparated(claims.get(APP_ROLES).textValue());
        return UserInfo.builder()
                       .id(claims.get(SUB).textValue())
                       .name(claims.get(NAME).textValue())
                       .email(claims.get(EMAIL).textValue())
                       .authorities(authorities)
                       .jurisdictions(claims.get(APP_JURISDICTIONS).textValue().split(","))
                       .preferences(extractPreferences(claims))
                       .organisationProfiles(extractProfiles(claims))
                       .build();
    }

    private UserPreferences extractPreferences(Map<String, JsonNode> claims) {
        return UserPreferences.builder()
                              .defaultJurisdiction(
                                      claims.get(USER_DEFAULT_JURISDICTION).textValue())
                              .defaultCaseType(
                                      claims.get(USER_DEFAULT_CASE_TYPE).textValue())
                              .defaultState(
                                      claims.get(USER_DEFAULT_STATE).textValue())
                              .build();
    }

    private Map<String, OrganisationProfile> extractProfiles(Map<String, JsonNode> claims) {
        return Optional.ofNullable(claims.get(APP_ORGANISATIONS))
                       .map(ORG_PARSER::parse)
                       .orElse(Collections.emptyMap());
    }
}
