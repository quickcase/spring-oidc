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
        @DisplayName("should provide root configuration")
        void shouldProvideRootConfiguration() {
            assertAll(
                    () -> assertThat(oidcConfig.getJwkSetUri(), equalTo("https://oidc.provider/jwkset")),
                    () -> assertThat(oidcConfig.getUserInfoUri(), equalTo("https://oidc.provider/userinfo")),
                    () -> assertThat(oidcConfig.getOpenidScope(), equalTo("custom-openid"))
            );
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

        @Test
        @DisplayName("should provide overridden private claims prefix")
        void shouldProvidePrivateClaimsPrefixOverride() {
            final String prefix = oidcConfig.getClaims().getPrefix();
            assertThat(prefix, equalTo("custom-prefix:"));
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
        @DisplayName("should provide root configuration")
        void shouldProvideRootConfiguration() {
            assertThat(oidcConfig.getOpenidScope(), equalTo("openid"));
        }

        @Test
        @DisplayName("should provide default claim names")
        void shouldProvideDefaultClaims() {
            final OidcConfig.ClaimNames names = oidcConfig.getClaims().getNames();
            assertAll(
                    () -> assertThat(names.getSub(), equalTo("sub")),
                    () -> assertThat(names.getName(), equalTo("name")),
                    () -> assertThat(names.getEmail(), equalTo("email")),
                    () -> assertThat(names.getRoles(), equalTo("app.quickcase.claims/roles")),
                    () -> assertThat(names.getOrganisations(), equalTo("app.quickcase.claims/organisations")),
                    () -> assertThat(names.getDefaultJurisdiction(), equalTo("app.quickcase.claims/default_jurisdiction")),
                    () -> assertThat(names.getDefaultCaseType(), equalTo("app.quickcase.claims/default_case_type")),
                    () -> assertThat(names.getDefaultState(), equalTo("app.quickcase.claims/default_state"))
            );
        }

        @Test
        @DisplayName("should provide default private claims prefix")
        void shouldProvideDefaultPrivateClaimsPrefix() {
            final String prefix = oidcConfig.getClaims().getPrefix();
            assertThat(prefix, equalTo(""));
        }
    }
}