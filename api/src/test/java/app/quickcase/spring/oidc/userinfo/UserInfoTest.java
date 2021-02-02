package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static app.quickcase.spring.oidc.AccessLevel.GROUP;
import static app.quickcase.spring.oidc.SecurityClassification.PRIVATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("UserInfo")
class UserInfoTest {
    private static final String SUBJECT = "user-123";

    @Test
    @DisplayName("should return email as username when provided")
    void shouldReturnEmailAsUsername() {
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .email("hello@quickcase.app")
                                          .build();

        assertThat(userInfo.getUsername(), equalTo("hello@quickcase.app"));
    }

    @Test
    @DisplayName("should return subject as username when email not provided")
    void shouldReturnSubjectAsUsername() {
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .build();

        assertThat(userInfo.getUsername(), equalTo(SUBJECT));
    }

    @Test
    @DisplayName("should return name when provided")
    void shouldReturnName() {
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .name("John Doe")
                                          .build();

        assertThat(userInfo.getName(), equalTo("John Doe"));
    }

    @Test
    @DisplayName("should return subject as name when not provided")
    void shouldReturnSubjectAsNameWhenMissing() {
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .build();

        assertThat(userInfo.getName(), equalTo(SUBJECT));
    }

    @Test
    @DisplayName("should return jurisdictions from organisations for backward compatibility")
    void shouldReturnJurisdictionBackwardCompat() {
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .organisationProfile("Juris-1", OrganisationProfile.builder().build())
                                          .organisationProfile("Juris-2", OrganisationProfile.builder().build())
                                          .build();

        assertThat(userInfo.getJurisdictions(), hasItems("Juris-1", "Juris-2"));
    }

    @Test
    @DisplayName("should return empty jurisdictions when no organisations")
    void shouldReturnEmptyJurisdictionWhenNoOrganisations() {
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .build();

        assertThat(userInfo.getJurisdictions(), equalTo(Collections.EMPTY_SET));
    }

    @Test
    @DisplayName("should give organisation profile when found")
    void getOrganisationProfileWhenFound() {
        final OrganisationProfile orgProfile = OrganisationProfile.builder()
                                                                  .accessLevel(GROUP)
                                                                  .group("org-1-group")
                                                                  .securityClassification(PRIVATE)
                                                                  .build();
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .organisationProfile("org-1", orgProfile)
                                          .build();
        final OrganisationProfile actualProfile = userInfo.getOrganisationProfiles().get("org-1");

        assertAll(
                () -> assertThat(actualProfile.getAccessLevel(), is(GROUP)),
                () -> assertThat(actualProfile.getSecurityClassification(), is(PRIVATE)),
                () -> assertThat(actualProfile.getGroup().get(), equalTo("org-1-group"))
        );
    }

    @Test
    @DisplayName("should find organisation profile regardless of case")
    void getOrganisationProfileIgnoreCase() {
        final OrganisationProfile orgProfile = OrganisationProfile.builder().build();
        final UserInfo userInfo = UserInfo.builder(SUBJECT)
                                          .organisationProfile("org-1", orgProfile)
                                          .build();
        final OrganisationProfile actualProfile = userInfo.getOrganisationProfiles().get("OrG-1");

        assertThat("Not handling organisation IDs as case-insensitive",
                   actualProfile,
                   equalTo(orgProfile));
    }

}