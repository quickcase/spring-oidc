package app.quickcase.spring.oidc.organisation;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.SecurityClassification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class OrganisationProfileTest {

    @Test
    @DisplayName("should create organisation profile")
    void shouldCreateOrganisationProfile() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .accessLevel(AccessLevel.GROUP)
                                                               .securityClassification(SecurityClassification.RESTRICTED)
                                                               .group("hello-group")
                                                               .build();

        assertThat(profile.getAccessLevel(), Matchers.is(AccessLevel.GROUP));
        assertThat(profile.getSecurityClassification(), Matchers.is(SecurityClassification.RESTRICTED));
        assertThat(profile.getGroup().orElse("N/A"), equalTo("hello-group"));
    }

    @Test
    @DisplayName("should ignore null access level")
    void shouldIgnoreNullAccessLevel() {
        final OrganisationProfile profile = OrganisationProfile.builder().accessLevel(null).build();

        assertThat(profile.getAccessLevel(), Matchers.is(AccessLevel.INDIVIDUAL));
    }

    @Test
    @DisplayName("should ignore null security classification")
    void shouldIgnoreNullSecurityClassification() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .securityClassification(null)
                                                               .build();

        assertThat(profile.getSecurityClassification(), Matchers.is(SecurityClassification.PUBLIC));
    }

    @Test
    @DisplayName("should ignore group access level when no group defined")
    void shouldIgnoreGroupAccessWhenNoGroupDefine() {
        final OrganisationProfile profile = OrganisationProfile.builder()
                                                               .accessLevel(AccessLevel.GROUP)
                                                               .build();

        assertThat(profile.getAccessLevel(), Matchers.is(AccessLevel.INDIVIDUAL));
    }

}