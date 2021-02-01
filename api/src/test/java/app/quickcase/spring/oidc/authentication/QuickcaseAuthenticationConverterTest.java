package app.quickcase.spring.oidc.authentication;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import app.quickcase.spring.oidc.userinfo.UserInfoService;
import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.SecurityClassification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("QuickcaseAuthenticationConverter")
class QuickcaseAuthenticationConverterTest {
    private static final String ACCESS_TOKEN = "token123";
    private static final String CLIENT_ID = "clientId";
    private static final String SCOPE_1 = "scope-1";
    private static final String SCOPE_2 = "scope-2";
    private static final String USER_ID = "user-456";
    private static final String USER_NAME = "Johnny Walker";
    private static final String USER_EMAIL = "jw@quickcase.app";
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";

    private UserInfoService userInfoServiceStub;
    private QuickcaseAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        final OrganisationProfile orgA = OrganisationProfile.builder()
                                                            .accessLevel(AccessLevel.GROUP)
                                                            .securityClassification(SecurityClassification.PRIVATE)
                                                            .group("org-a-group")
                                                            .build();
        userInfoServiceStub = (sub, token) -> UserInfo.builder()
                                                      .id(sub)
                                                      .name(USER_NAME)
                                                      .email(USER_EMAIL)
                                                      .authorities(ROLE_1, ROLE_2)
                                                      .organisationProfile("org-a", orgA)
                                                      .build();

        converter = new QuickcaseAuthenticationConverter(userInfoServiceStub);
    }

    @Nested
    @DisplayName("when client credentials")
    class WhenClientCredentials {

        private QuickcaseAuthentication clientAuthentication() {
            final Jwt jwt = Jwt.withTokenValue(ACCESS_TOKEN)
                               .header("alg", "HS256")
                               .claim("sub", CLIENT_ID)
                               .claim("scope", scopes(SCOPE_1, SCOPE_2))
                               .claim("client_id", CLIENT_ID)
                               .build();

            return converter.convert(jwt);
        }

        @Test
        @DisplayName("should get ID from client")
        void shouldGetIdFromClient() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.getId(), equalTo(CLIENT_ID));
        }

        @Test
        @DisplayName("should use scopes as authorities")
        void shouldUseScopesAsAuthorities() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            final GrantedAuthority[] scopes = authorities(SCOPE_1, SCOPE_2);
            assertThat(authentication.getAuthorities(), containsInAnyOrder(scopes));
        }

        @Test
        @DisplayName("should have original access token")
        void shouldHaveOriginalAccessToken() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.getAccessToken(), equalTo(ACCESS_TOKEN));
        }

        @Test
        @DisplayName("should be client authentication")
        void shouldBeClientAuthentication() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication, instanceOf(QuickcaseClientAuthentication.class));
        }
    }

    @Nested
    @DisplayName("when user credentials")
    class WhenUserCredentials {

        private QuickcaseAuthentication userAuthentication() {
            return userAuthentication(QuickcaseAuthenticationConverter.OPENID_SCOPE);
        }

        private QuickcaseAuthentication userAuthentication(String openidScope) {
            final Jwt jwt = Jwt.withTokenValue(ACCESS_TOKEN)
                               .header("alg", "HS256")
                               .claim("sub", USER_ID)
                               .claim("scope", scopes(openidScope, SCOPE_2))
                               .claim("client_id", CLIENT_ID)
                               .build();

            return converter.convert(jwt);
        }

        @Test
        @DisplayName("should get ID from user")
        void shouldGetIdFromUser() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getId(), equalTo(USER_ID));
        }

        @Test
        @DisplayName("should use user name")
        void shouldUseUserName() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getName(), equalTo(USER_NAME));
        }

        @Test
        @DisplayName("should use user email")
        void shouldHaveEmail() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getEmail().get(), equalTo(USER_EMAIL));
        }

        @Test
        @DisplayName("should have user info")
        void shouldHaveUserInfo() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getUserInfo()
                                     .get()
                                     .getOrganisationProfiles(), aMapWithSize(1));
        }

        @Test
        @DisplayName("should use user roles as authorities")
        void shouldUseRolesAsAuthorities() {
            final QuickcaseAuthentication authentication = userAuthentication();

            final GrantedAuthority[] roles = authorities(ROLE_1, ROLE_2);
            assertThat(authentication.getAuthorities(), containsInAnyOrder(roles));
        }

        @Test
        @DisplayName("should have original access token")
        void shouldHaveOriginalAccessToken() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getAccessToken(), equalTo(ACCESS_TOKEN));
        }

        @Test
        @DisplayName("should be user authentication")
        void shouldBeUserAuthentication() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication, instanceOf(QuickcaseUserAuthentication.class));
        }

        @Test
        @DisplayName("should extract organisation profiles")
        void shouldExtractOrganisationProfiles() {
            final QuickcaseAuthentication authentication = userAuthentication();

            final OrganisationProfile profile = authentication.getOrganisationProfile("org-a");

            Assertions.assertAll(
                    () -> assertThat(profile.getAccessLevel(), Matchers.is(AccessLevel.GROUP)),
                    () -> assertThat(profile.getSecurityClassification(), Matchers.is(SecurityClassification.PRIVATE)),
                    () -> assertThat(profile.getGroup().orElse("N/A"), equalTo("org-a-group"))
            );
        }

        @Test
        @DisplayName("should accept custom scope for openid")
        void shouldAcceptCustomOpenIdScope() {
            converter = new QuickcaseAuthenticationConverter(userInfoServiceStub, "custom-openid");

            final QuickcaseAuthentication authentication = userAuthentication("custom-openid");

            assertThat(authentication, instanceOf(QuickcaseUserAuthentication.class));
        }
    }

    private String scopes(String... items) {
        return String.join(" ", items);
    }

    private GrantedAuthority[] authorities(String ...authorities) {
        return Arrays.stream(authorities)
                     .map(SimpleGrantedAuthority::new)
                     .toArray(GrantedAuthority[]::new);
    }
}