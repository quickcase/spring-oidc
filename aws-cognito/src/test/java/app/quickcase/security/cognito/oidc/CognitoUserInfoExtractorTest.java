package app.quickcase.security.cognito.oidc;

import app.quickcase.security.UserInfo;
import app.quickcase.security.UserPreferences;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.Map;

import static app.quickcase.security.cognito.CognitoClaims.APP_JURISDICTIONS;
import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static app.quickcase.security.cognito.CognitoClaims.EMAIL;
import static app.quickcase.security.cognito.CognitoClaims.NAME;
import static app.quickcase.security.cognito.CognitoClaims.SUB;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.security.cognito.CognitoClaims.USER_DEFAULT_STATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CognitoUserInfoExtractor")
class CognitoUserInfoExtractorTest {
    private static final String USER_APP_ROLES = "role1,role2";
    private static final String USER_ID = "eec55037-bac7-46b4-9849-f063e627e4f3";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@quickcase.app";
    private static final String USER_JURISDICTIONS = "jid1,jid2";
    private static final String DEFAULT_JURISDICTION = "jid1";
    private static final String DEFAULT_CASE_TYPE = "ct1";
    private static final String DEFAULT_STATE = "stateA";

    @Test
    @DisplayName("should extract userInfo from claims")
    void shouldExtractUserInfo() {
        final UserInfo userInfo = new CognitoUserInfoExtractor().extract(claims());

        assertThat(userInfo, is(notNullValue()));
        assertAll(
                () -> assertThat(userInfo.getId(), equalTo(USER_ID)),
                () -> assertThat(userInfo.getName(), equalTo(USER_NAME)),
                () -> assertThat(userInfo.getEmail(), equalTo(USER_EMAIL)),
                () -> assertThat(userInfo.getAuthorities(), containsInAnyOrder(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                )),
                () -> assertThat(userInfo.getJurisdictions(),
                                 containsInAnyOrder("jid1", "jid2")),
                () -> assertPreferences(userInfo.getPreferences())
        );
    }

    private Map<String, Object> claims() {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, USER_ID);
        claims.put(NAME, USER_NAME);
        claims.put(EMAIL, USER_EMAIL);
        claims.put(APP_ROLES, USER_APP_ROLES);
        claims.put(APP_JURISDICTIONS, USER_JURISDICTIONS);
        claims.put(USER_DEFAULT_JURISDICTION, DEFAULT_JURISDICTION);
        claims.put(USER_DEFAULT_CASE_TYPE, DEFAULT_CASE_TYPE);
        claims.put(USER_DEFAULT_STATE, DEFAULT_STATE);
        return claims;
    }

    private void assertPreferences(UserPreferences preferences) {
        assertAll(
                () -> assertThat(preferences.getDefaultJurisdiction(), equalTo(DEFAULT_JURISDICTION)),
                () -> assertThat(preferences.getDefaultCaseType(), equalTo(DEFAULT_CASE_TYPE)),
                () -> assertThat(preferences.getDefaultState(), equalTo(DEFAULT_STATE))
        );
    }
}