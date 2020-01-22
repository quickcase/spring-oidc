package app.quickcase.security.authentication;

import app.quickcase.security.AccessLevel;
import app.quickcase.security.organisation.OrganisationProfile;
import app.quickcase.security.SecurityClassification;
import app.quickcase.security.utils.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

class QuickcaseClientAuthenticationTest {
    private static final String ACCESS_TOKEN = "access-token-123";
    private static final String CLIENT_ID = "client-123";

    @Test
    @DisplayName("should not have email address")
    void getEmail() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getEmail().isPresent(), is(false));
    }

    @Test
    @DisplayName("should use OAuth2 client ID as identifier")
    void getId() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getId(), equalTo(CLIENT_ID));
    }

    @Test
    @DisplayName("should not have user info")
    void getUserInfo() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getUserInfo().isPresent(), is(false));
    }

    @Test
    @DisplayName("should be flagged as authenticated")
    void isAuthenticated() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.isAuthenticated(), is(true));
    }

    @Test
    @DisplayName("should be flagged as client only")
    void isClientOnly() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.isClientOnly(), is(true));
    }

    @Test
    @DisplayName("should use access token as credentials")
    void getCredentials() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getCredentials(), equalTo(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should have access token")
    void getAccessToken() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getAccessToken(), equalTo(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should use default name")
    void getName() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getName(), equalTo("System"));
    }

    @Test
    @DisplayName("should use client ID as principal")
    void getPrincipal() {
        final QuickcaseAuthentication auth = clientAuthentication();
        assertThat(auth.getPrincipal(), equalTo(CLIENT_ID));
    }

    @Test
    @DisplayName("should always give client's default organisation profile")
    void getOrganisationProfile() {
        final QuickcaseAuthentication auth = clientAuthentication();
        final OrganisationProfile orgProfile = auth.getOrganisationProfile("anyOrg");

        assertAll(
                () -> assertThat(orgProfile.getAccessLevel(), is(AccessLevel.ORGANISATION)),
                () -> assertThat(orgProfile.getSecurityClassification(),
                                 is(SecurityClassification.PUBLIC)),
                () -> assertThat(orgProfile.getGroup().isPresent(), is(false))
        );
    }

    private QuickcaseAuthentication clientAuthentication() {
        final Set<GrantedAuthority> authorities = StringUtils.authorities("ROLE-1", "ROLE-2");
        return new QuickcaseClientAuthentication(ACCESS_TOKEN, CLIENT_ID, authorities);
    }
}