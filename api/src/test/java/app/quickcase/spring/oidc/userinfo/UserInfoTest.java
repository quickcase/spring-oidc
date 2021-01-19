package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@DisplayName("UserInfo")
class UserInfoTest {

    @Test
    @DisplayName("should return jurisdictions from organisations for backward compatibility")
    void shouldReturnJurisdictionBackwardCompat() {
        final UserInfo userInfo = UserInfo.builder()
                                          .organisationProfile("Juris-1", OrganisationProfile.builder().build())
                                          .organisationProfile("Juris-2", OrganisationProfile.builder().build())
                                          .build();

        assertThat(userInfo.getJurisdictions(), hasItems("Juris-1", "Juris-2"));
    }

    @Test
    @DisplayName("should return empty jurisdictions when no organisations")
    void shouldReturnEmptyJurisdictionWhenNoOrganisations() {
        final UserInfo userInfo = new UserInfo(null, null, null, null, null, null);

        assertThat(userInfo.getJurisdictions(), equalTo(Collections.EMPTY_SET));
    }

}