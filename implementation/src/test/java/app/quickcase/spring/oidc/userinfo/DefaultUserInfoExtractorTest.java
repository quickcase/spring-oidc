package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.claims.ClaimNamesProvider;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DefaultUserInfoExtractor")
class DefaultUserInfoExtractorTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String CLAIM_SUB = "conf-sub";
    private static final String CLAIM_NAME = "conf-name";
    private static final String CLAIM_EMAIL = "conf-email";
    private static final String CLAIM_ROLES = "conf-roles";
    private static final String CLAIM_ORGS = "conf-orgs";
    private static final String CLAIM_DEF_JURISDICTION = "conf-jurisdiction";
    private static final String CLAIM_DEF_CASE_TYPE = "conf-case-type";
    private static final String CLAIM_DEF_STATE = "conf-state";

    private static final String USER_APP_ROLES = "role1,role2";
    private static final String USER_ID = "eec55037-bac7-46b4-9849-f063e627e4f3";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@quickcase.app";
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
        final UserInfo userInfo = new DefaultUserInfoExtractor(claimNamesProvider()).extract(claims());

        assertThat(userInfo, is(notNullValue()));
        assertAll(
                () -> assertThat(userInfo.getSubject(), equalTo(USER_ID)),
                () -> assertThat(userInfo.getName(), equalTo(USER_NAME)),
                () -> assertThat(userInfo.getEmail().get(), equalTo(USER_EMAIL)),
                () -> assertThat(userInfo.getAuthorities(), containsInAnyOrder(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                )),
                () -> assertThat(userInfo.getJurisdictions(),
                        containsInAnyOrder("org-1", "org-2"))
        );
    }

    @Test
    @DisplayName("should extract user preferences")
    void shouldExtractUserPreferences() throws Exception {
        final UserInfo userInfo = new DefaultUserInfoExtractor(claimNamesProvider()).extract(claims());
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
        final UserInfo userInfo = new DefaultUserInfoExtractor(claimNamesProvider()).extract(claims());

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
        final UserInfo userInfo = new DefaultUserInfoExtractor(claimNamesProvider()).extract(minimumClaims());

        assertThat(userInfo, is(notNullValue()));
        assertAll(
                () -> assertThat(userInfo.getSubject(), equalTo(USER_ID)),
                () -> assertThat(userInfo.getName(), equalTo(USER_ID)),
                () -> assertThat(userInfo.getEmail().isEmpty(), is(true))
        );
    }

    @Test
    @DisplayName("should throw exception when `sub` claim missing")
    void shouldThrowExceptionWhenNoSubClaim() throws Exception {
        final Map<String, JsonNode> claims = minimumClaims();
        claims.remove(CLAIM_SUB);

        assertThrows(OidcException.class,
                     () -> new DefaultUserInfoExtractor(claimNamesProvider()).extract(claims),
                     "Mandatory 'sub' claim missing");
    }

    private Map<String, JsonNode> claims() {
        final Map<String, JsonNode> claims = new HashMap<>();
        claims.put(CLAIM_SUB, textNode(USER_ID));
        claims.put(CLAIM_NAME, textNode(USER_NAME));
        claims.put(CLAIM_EMAIL, textNode(USER_EMAIL));
        claims.put(CLAIM_ROLES, textNode(USER_APP_ROLES));
        claims.put(CLAIM_ORGS, USER_ORGANISATIONS);
        claims.put(CLAIM_DEF_JURISDICTION, textNode(DEFAULT_JURISDICTION));
        claims.put(CLAIM_DEF_CASE_TYPE, textNode(DEFAULT_CASE_TYPE));
        claims.put(CLAIM_DEF_STATE, textNode(DEFAULT_STATE));
        return claims;
    }

    private Map<String, JsonNode> minimumClaims() {
        final Map<String, JsonNode> claims = new HashMap<>();
        claims.put(CLAIM_SUB, textNode(USER_ID));
        return claims;
    }

    private JsonNode textNode(String value) {
        return new TextNode(value);
    }

    private ClaimNamesProvider claimNamesProvider() {
        return new ClaimNamesProvider() {
            @Override
            public String sub() {
                return CLAIM_SUB;
            }

            @Override
            public String name() {
                return CLAIM_NAME;
            }

            @Override
            public String email() {
                return CLAIM_EMAIL;
            }

            @Override
            public String roles() {
                return CLAIM_ROLES;
            }

            @Override
            public String organisations() {
                return CLAIM_ORGS;
            }

            @Override
            public String defaultJurisdiction() {
                return CLAIM_DEF_JURISDICTION;
            }

            @Override
            public String defaultCaseType() {
                return CLAIM_DEF_CASE_TYPE;
            }

            @Override
            public String defaultState() {
                return CLAIM_DEF_STATE;
            }
        };
    }

}