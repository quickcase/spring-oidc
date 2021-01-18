package app.quickcase.spring.oidc.keycloak.oidc;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import app.quickcase.spring.oidc.userinfo.UserPreferences;
import app.quickcase.spring.oidc.OidcException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.Map;

import static app.quickcase.spring.oidc.AccessLevel.GROUP;
import static app.quickcase.spring.oidc.AccessLevel.ORGANISATION;
import static app.quickcase.spring.oidc.SecurityClassification.PRIVATE;
import static app.quickcase.spring.oidc.SecurityClassification.PUBLIC;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.APP_JURISDICTIONS;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.APP_ORGANISATIONS;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.APP_ROLES;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.EMAIL;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.NAME;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.SUB;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.USER_DEFAULT_CASE_TYPE;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.USER_DEFAULT_JURISDICTION;
import static app.quickcase.spring.oidc.keycloak.KeycloakClaims.USER_DEFAULT_STATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("KeycloakUserInfoExtractor")
class KeycloakUserInfoExtractorTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String USER_APP_ROLES = "role1,role2";
    private static final String USER_ID = "eec55037-bac7-46b4-9849-f063e627e4f3";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@quickcase.app";
    private static final String USER_JURISDICTIONS = "jid1,jid2";
    private static final String DEFAULT_JURISDICTION = "jid1";
    private static final String DEFAULT_CASE_TYPE = "ct1";
    private static final String DEFAULT_STATE = "stateA";
    private static final JsonNode USER_ORGANISATIONS;

    static {
        try {
            USER_ORGANISATIONS = MAPPER.readTree("{" +
                        "\"org-1\": {\"access\": \"organisation\", \"classification\": \"private\"}," +
                        "\"org-2\": {\"access\": \"group\", \"classification\": \"public\", \"group\": \"group-1\"}" +
                        "}");
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    @DisplayName("should extract userInfo from claims")
    void shouldExtractUserInfo() throws Exception {
        final UserInfo userInfo = new KeycloakUserInfoExtractor().extract(claims());

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
                        containsInAnyOrder("jid1", "jid2"))
        );
    }

    @Test
    @DisplayName("should extract user preferences")
    void shouldExtractUserPreferences() throws Exception {
        final UserInfo userInfo = new KeycloakUserInfoExtractor().extract(claims());
        final UserPreferences preferences = userInfo.getPreferences();

        assertAll(
                () -> assertThat(preferences.getDefaultJurisdiction(), equalTo(DEFAULT_JURISDICTION)),
                () -> assertThat(preferences.getDefaultCaseType(), equalTo(DEFAULT_CASE_TYPE)),
                () -> assertThat(preferences.getDefaultState(), equalTo(DEFAULT_STATE))
        );
    }

    @Test
    @DisplayName("should extract organisation profiles")
    void shouldExtractOrganisationProfiles() throws Exception {
        final UserInfo userInfo = new KeycloakUserInfoExtractor().extract(claims());

        final Map<String, OrganisationProfile> profiles = userInfo.getOrganisationProfiles();
        assertThat(profiles.size(), is(2));

        final OrganisationProfile profile1 = profiles.get("org-1");
        assertAll(
                () -> assertThat(profile1.getAccessLevel(), is(ORGANISATION)),
                () -> assertThat(profile1.getSecurityClassification(), is(PRIVATE)),
                () -> assertThat(profile1.getGroup().isPresent(), is(false))
        );

        final OrganisationProfile profile2 = profiles.get("org-2");
        assertAll(
                () -> assertThat(profile2.getAccessLevel(), is(GROUP)),
                () -> assertThat(profile2.getSecurityClassification(), is(PUBLIC)),
                () -> assertThat(profile2.getGroup().orElse("N/A"), equalTo("group-1"))
        );
    }

    @Test
    @DisplayName("should expect most claims to be optional")
    void shouldExpectClaimsToBeOptional() throws Exception {
        final UserInfo userInfo = new KeycloakUserInfoExtractor().extract(minimumClaims());

        assertThat(userInfo, is(notNullValue()));
        assertAll(
                () -> assertThat(userInfo.getId(), equalTo(USER_ID)),
                () -> assertThat(userInfo.getEmail(), equalTo(USER_EMAIL))
        );
    }

    @Test
    @DisplayName("should throw exception when `sub` claim missing")
    void shouldThrowExceptionWhenNoSubClaim() throws Exception {
        final Map<String, JsonNode> claims = minimumClaims();
        claims.remove(SUB);

        assertThrows(OidcException.class,
                     () -> new KeycloakUserInfoExtractor().extract(claims),
                     "Mandatory 'sub' claim missing");
    }

    @Test
    @DisplayName("should throw exception when `email` claim missing")
    void shouldThrowExceptionWhenNoEmailClaim() throws Exception {
        final Map<String, JsonNode> claims = minimumClaims();
        claims.remove(EMAIL);

        assertThrows(OidcException.class,
                     () -> new KeycloakUserInfoExtractor().extract(claims),
                     "Mandatory 'email' claim missing");
    }

    private Map<String, JsonNode> claims() {
        final Map<String, JsonNode> claims = new HashMap<>();
        claims.put(SUB, getTextNode(USER_ID));
        claims.put(NAME, getTextNode(USER_NAME));
        claims.put(EMAIL, getTextNode(USER_EMAIL));
        claims.put(APP_ROLES, getTextNode(USER_APP_ROLES));
        claims.put(APP_JURISDICTIONS, getTextNode(USER_JURISDICTIONS));
        claims.put(USER_DEFAULT_JURISDICTION, getTextNode(DEFAULT_JURISDICTION));
        claims.put(USER_DEFAULT_CASE_TYPE, getTextNode(DEFAULT_CASE_TYPE));
        claims.put(USER_DEFAULT_STATE, getTextNode(DEFAULT_STATE));
        claims.put(APP_ORGANISATIONS, USER_ORGANISATIONS);
        return claims;
    }

    private Map<String, JsonNode> minimumClaims() {
        final Map<String, JsonNode> claims = new HashMap<>();
        claims.put(SUB, getTextNode(USER_ID));
        claims.put(EMAIL, getTextNode(USER_EMAIL));
        return claims;
    }

    private JsonNode getTextNode(String value) {
        return new TextNode(value);
    }

}