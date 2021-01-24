package app.quickcase.spring.oidc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class OidcConfigTest {

    @Nested
    @SpringBootTest
    @ActiveProfiles("default")
    @EnableConfigurationProperties(OidcConfig.class)
    @DisplayName("when fully configured")
    class OidcConfigTest_Default {
        @Autowired
        private OidcConfig oidcConfig;

        @Test
        @DisplayName("should provide user-info-uri")
        void shouldProvideRootConfiguration() {
            assertThat(oidcConfig.getUserInfoUri(), equalTo("https://oidc.provider/userinfo"));
        }

        @Test
        @DisplayName("should provide overridden claim names")
        void shouldProvideClaimsOverrides() {
            final OidcConfig.ClaimNames names = oidcConfig.getClaims().getNames();
            assertAll(
                    () -> assertThat(names.getSub(), equalTo("custom-sub")),
                    () -> assertThat(names.getName(), equalTo("custom-name")),
                    () -> assertThat(names.getEmail(), equalTo("custom-email")),
                    () -> assertThat(names.getRoles(), equalTo("custom-roles")),
                    () -> assertThat(names.getOrganisations(), equalTo("custom-organisations")),
                    () -> assertThat(names.getDefaultJurisdiction(), equalTo("custom-default-jurisdiction")),
                    () -> assertThat(names.getDefaultCaseType(), equalTo("custom-default-case-type")),
                    () -> assertThat(names.getDefaultState(), equalTo("custom-default-state"))
            );
        }
    }

    @Nested
    @SpringBootTest
    @ActiveProfiles("partial")
    @EnableConfigurationProperties(OidcConfig.class)
    @DisplayName("when partially configured")
    class OidcConfigTest_Partial {
        @Autowired
        private OidcConfig oidcConfig;

        @Test
        @DisplayName("should provide default claim names")
        void shouldProvideClaimsOverrides() {
            final OidcConfig.ClaimNames names = oidcConfig.getClaims().getNames();
            assertAll(
                    () -> assertThat(names.getSub(), equalTo("sub")),
                    () -> assertThat(names.getName(), equalTo("name")),
                    () -> assertThat(names.getEmail(), equalTo("email")),
                    () -> assertThat(names.getRoles(), equalTo("app_roles")),
                    () -> assertThat(names.getOrganisations(), equalTo("app_organisations")),
                    () -> assertThat(names.getDefaultJurisdiction(), equalTo("default_jurisdiction")),
                    () -> assertThat(names.getDefaultCaseType(), equalTo("default_case_type")),
                    () -> assertThat(names.getDefaultState(), equalTo("default_state"))
            );
        }
    }
}