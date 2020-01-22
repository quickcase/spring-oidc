package app.quickcase.security.organisation;

import app.quickcase.security.organisation.OrganisationProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static app.quickcase.security.AccessLevel.*;
import static app.quickcase.security.SecurityClassification.PUBLIC;
import static app.quickcase.security.SecurityClassification.RESTRICTED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class OrganisationProfileTest {

    @Test
    @DisplayName("should create organisation profile")
    void shouldCreateOrganisationProfile() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .accessLevel(GROUP)
                                                               .securityClassification(RESTRICTED)
                                                               .group("hello-group")
                                                               .build();

        assertThat(profile.getAccessLevel(), is(GROUP));
        assertThat(profile.getSecurityClassification(), is(RESTRICTED));
        assertThat(profile.getGroup().orElse("N/A"), equalTo("hello-group"));
    }

    @Test
    @DisplayName("should ignore null access level")
    void shouldIgnoreNullAccessLevel() {
        final OrganisationProfile profile = OrganisationProfile.builder().accessLevel(null).build();

        assertThat(profile.getAccessLevel(), is(INDIVIDUAL));
    }

    @Test
    @DisplayName("should ignore null security classification")
    void shouldIgnoreNullSecurityClassification() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .securityClassification(null)
                                                               .build();

        assertThat(profile.getSecurityClassification(), is(PUBLIC));
    }

    @Test
    @DisplayName("should ignore group access level when no group defined")
    void shouldIgnoreGroupAccessWhenNoGroupDefine() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .accessLevel(GROUP)
                                                               .build();

        assertThat(profile.getAccessLevel(), is(INDIVIDUAL));
    }

}