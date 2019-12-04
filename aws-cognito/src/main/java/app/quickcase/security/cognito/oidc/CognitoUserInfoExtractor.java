package app.quickcase.security.cognito.oidc;

import app.quickcase.security.UserInfo;
import app.quickcase.security.UserPreferences;
import app.quickcase.security.oidc.UserInfoExtractor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Set;

import static app.quickcase.security.cognito.CognitoClaims.APP_JURISDICTIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static app.quickcase.security.cognito.CognitoClaims.EMAIL;
import static app.quickcase.security.cognito.CognitoClaims.NAME;
import static app.quickcase.security.cognito.CognitoClaims.SUB;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_STATE;
import static app.quickcase.security.utils.StringUtils.fromCommaSeparated;

public class CognitoUserInfoExtractor implements UserInfoExtractor {
    @Override
    public UserInfo extract(Map<String, Object> claims) {
        return extractUserInfo(claims);
    }

    public UserInfo extractUserInfo(Map<String, Object> claims) {
        Set<GrantedAuthority> authorities = fromCommaSeparated(String.valueOf(claims.get(APP_ROLES)));
        return UserInfo.builder()
                       .id(String.valueOf(claims.get(SUB)))
                       .name(String.valueOf(claims.get(NAME)))
                       .email(String.valueOf(claims.get(EMAIL)))
                       .authorities(authorities)
                       .jurisdictions(String.valueOf(claims.get(APP_JURISDICTIONS)).split(","))
                       .preferences(extractPreferences(claims))
                       .build();
    }

    private UserPreferences extractPreferences(Map<String, Object> claims) {
        return UserPreferences.builder()
                              .defaultJurisdiction(
                                      String.valueOf(claims.get(USER_DEFAULT_JURISDICTION)))
                              .defaultCaseType(
                                      String.valueOf(claims.get(USER_DEFAULT_CASE_TYPE)))
                              .defaultState(
                                      String.valueOf(claims.get(USER_DEFAULT_STATE)))
                              .build();
    }
}
