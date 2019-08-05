package app.quickcase.security.keycloak;

import app.quickcase.security.UserAuthenticationToken;
import app.quickcase.security.UserInfo;
import app.quickcase.security.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.Map;

import static app.quickcase.security.keycloak.KeycloakClaims.APP_JURISDICTIONS;
import static app.quickcase.security.keycloak.KeycloakClaims.APP_ROLES;
import static app.quickcase.security.keycloak.KeycloakClaims.EMAIL;
import static app.quickcase.security.keycloak.KeycloakClaims.NAME;
import static app.quickcase.security.keycloak.KeycloakClaims.SUB;
import static app.quickcase.security.keycloak.KeycloakClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.security.keycloak.KeycloakClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.security.keycloak.KeycloakClaims.USER_DEFAULT_STATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

class KeycloakUserAuthenticationConverterTest {
    private static final String USER_ID = "fb09ed5f-bdee-4657-8dcb-891721b63eb2";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@quickcase.app";
    private static final String DEFAULT_JURISDICTION = "jid1";
    private static final String DEFAULT_CASE_TYPE = "ct1";
    private static final String DEFAULT_STATE = "stateA";

    private KeycloakUserAuthenticationConverter userConverter;

    @BeforeEach
    void setUp() {
        userConverter = new KeycloakUserAuthenticationConverter();
    }

    @Test
    @DisplayName("should extract authentication from claims")
    void shouldExtractAuthentication() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, USER_ID);
        claims.put(NAME, USER_NAME);
        claims.put(EMAIL, USER_EMAIL);
        claims.put(APP_ROLES, "role1,role2");
        claims.put(APP_JURISDICTIONS, "jid1,jid2");
        claims.put(USER_DEFAULT_JURISDICTION, DEFAULT_JURISDICTION);
        claims.put(USER_DEFAULT_CASE_TYPE, DEFAULT_CASE_TYPE);
        claims.put(USER_DEFAULT_STATE, DEFAULT_STATE);

        Authentication authentication = userConverter.extractAuthentication(claims);

        assertThat(authentication.getPrincipal(), instanceOf(UserInfo.class));
        UserInfo principal = (UserInfo) authentication.getPrincipal();

        assertAll(
                () -> assertThat(principal.getId(), equalTo(USER_ID)),
                () -> assertThat(principal.getName(), equalTo(USER_NAME)),
                () -> assertThat(principal.getEmail(), equalTo(USER_EMAIL)),
                () -> assertThat(authentication.getAuthorities(), containsInAnyOrder(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                )),
                () -> assertThat(principal.getJurisdictions(),
                                 containsInAnyOrder("jid1", "jid2")),
                () -> assertPreferences(principal.getPreferences())
        );
    }

    @Test
    @DisplayName("should convert user to claims")
    void shouldConvertToClaims() {
        UserPreferences preferences = UserPreferences.builder()
                                                     .defaultJurisdiction(DEFAULT_JURISDICTION)
                                                     .defaultCaseType(DEFAULT_CASE_TYPE)
                                                     .defaultState(DEFAULT_STATE)
                                                     .build();
        UserInfo user = UserInfo.builder()
                                .id(USER_ID)
                                .email(USER_EMAIL)
                                .name(USER_NAME)
                                .authorities("role1", "role2")
                                .jurisdictions("jid1", "jid2")
                                .preferences(preferences)
                                .build();
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(user);

        Map<String, ?> claims = userConverter.convertUserAuthentication(authenticationToken);

        assertAll(
                () -> assertThat(claims, is(aMapWithSize(8))),
                () -> assertThat(claims.get(SUB), equalTo(USER_ID)),
                () -> assertThat(claims.get(EMAIL), equalTo(USER_EMAIL)),
                () -> assertThat(claims.get(NAME), equalTo(USER_NAME)),
                () -> assertThat(claims.get(APP_ROLES), equalTo("role1,role2")),
                () -> assertThat(claims.get(APP_JURISDICTIONS), equalTo("jid2,jid1")),
                () -> assertThat(claims.get(USER_DEFAULT_JURISDICTION), equalTo(DEFAULT_JURISDICTION)),
                () -> assertThat(claims.get(USER_DEFAULT_CASE_TYPE), equalTo(DEFAULT_CASE_TYPE)),
                () -> assertThat(claims.get(USER_DEFAULT_STATE), equalTo(DEFAULT_STATE))
        );
    }

    private void assertPreferences(UserPreferences preferences) {
        assertAll(
                () -> assertThat(preferences.getDefaultJurisdiction(), equalTo(DEFAULT_JURISDICTION)),
                () -> assertThat(preferences.getDefaultCaseType(), equalTo(DEFAULT_CASE_TYPE)),
                () -> assertThat(preferences.getDefaultState(), equalTo(DEFAULT_STATE))
        );
    }
}