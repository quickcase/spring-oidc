package app.quickcase.spring.oidc.authentication;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.SecurityClassification;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuickcaseUserAuthenticationTest {
    private static final String ACCESS_TOKEN = "access-token-123";
    private static final String USER_ID = "client-123";
    private static final String USER_EMAIL = "test@test";
    private static final String USER_NAME = "Jean Paul";

    @Test
    @DisplayName("should enforce non-null fields")
    void shouldEnforceNonNullFields() {
        Assertions.assertAll(
                () -> assertThrows(NullPointerException.class, () -> new QuickcaseUserAuthentication(null, UserInfo.builder(USER_ID).build())),
                () -> assertThrows(NullPointerException.class, () -> new QuickcaseUserAuthentication(ACCESS_TOKEN, null))
        );
    }

    @Test
    @DisplayName("should have email address")
    void getEmail() {
        final QuickcaseAuthentication auth = userAuthentication();

        Assertions.assertAll(
                () -> assertThat(auth.getEmail().isPresent(), is(true)),
                () -> assertThat(auth.getEmail().get(), equalTo(USER_EMAIL))
        );
    }

    @Test
    @DisplayName("should use user ID as identifier")
    void getId() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.getId(), equalTo(USER_ID));
    }

    @Test
    @DisplayName("should have user info")
    void getUserInfo() {
        final QuickcaseAuthentication auth = userAuthentication();

        Assertions.assertAll(
                () -> assertThat(auth.getUserInfo().isPresent(), is(true)),
                () -> assertThat(auth.getUserInfo().flatMap(UserInfo::getEmail), equalTo(Optional.of(USER_EMAIL)))
        );
    }

    @Test
    @DisplayName("should be flagged as authenticated")
    void isAuthenticated() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.isAuthenticated(), is(true));
    }

    @Test
    @DisplayName("should NOT be flagged as client only")
    void isClientOnly() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.isClientOnly(), is(false));
    }

    @Test
    @DisplayName("should use access token as credentials")
    void getCredentials() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.getCredentials(), equalTo(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should have access token")
    void getAccessToken() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.getAccessToken(), equalTo(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should use user name")
    void getName() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.getName(), equalTo(USER_NAME));
    }

    @Test
    @DisplayName("should use user ID as principal")
    void getPrincipal() {
        final QuickcaseAuthentication auth = userAuthentication();
        assertThat(auth.getPrincipal(), equalTo(USER_ID));
    }

    @Test
    @DisplayName("should give default organisation profile when org not found")
    void getOrganisationProfileWhenNotFound() {
        final QuickcaseAuthentication auth = userAuthentication();
        final OrganisationProfile orgProfile = auth.getOrganisationProfile("anyOrg");

        assertAll(
                () -> assertThat(orgProfile.getAccessLevel(), Matchers.is(AccessLevel.INDIVIDUAL)),
                () -> assertThat(orgProfile.getSecurityClassification(),
                                 Matchers.is(SecurityClassification.PUBLIC)),
                () -> assertThat(orgProfile.getGroup().isPresent(), is(false))
        );
    }

    @Test
    @DisplayName("should give organisation profile when found")
    void getOrganisationProfileWhenFound() {
        final QuickcaseAuthentication auth = userAuthentication();
        final OrganisationProfile orgProfile = auth.getOrganisationProfile("org-1");

        assertAll(
                () -> assertThat(orgProfile.getAccessLevel(), Matchers.is(AccessLevel.GROUP)),
                () -> assertThat(orgProfile.getSecurityClassification(),
                                 Matchers.is(SecurityClassification.PRIVATE)),
                () -> assertThat(orgProfile.getGroup().get(), equalTo("org-1-group"))
        );
    }

    private QuickcaseAuthentication userAuthentication() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .accessLevel(AccessLevel.GROUP)
                                                               .group("org-1-group")
                                                               .securityClassification(SecurityClassification.PRIVATE)
                                                               .build();
        final UserInfo userInfo = UserInfo.builder(USER_ID)
                                          .name(USER_NAME)
                                          .email(USER_EMAIL)
                                          .authorities("ROLE-1", "ROLE-2")
                                          .organisationProfile("org-1", profile)
                                          .build();

        return new QuickcaseUserAuthentication(ACCESS_TOKEN, userInfo);
    }
}