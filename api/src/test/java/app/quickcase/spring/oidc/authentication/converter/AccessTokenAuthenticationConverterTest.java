package app.quickcase.spring.oidc.authentication.converter;

import java.util.Arrays;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthentication;
import app.quickcase.spring.oidc.authentication.QuickcaseClientAuthentication;
import app.quickcase.spring.oidc.authentication.QuickcaseUserAuthentication;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import app.quickcase.spring.oidc.userinfo.UserInfoExtractor;
import app.quickcase.spring.oidc.userinfo.UserPreferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

class AccessTokenAuthenticationConverterTest {
    private static final String ACCESS_TOKEN = "token123";
    private static final String CLIENT_ID = "clientId";
    private static final String SCOPE_1 = "scope-1";
    private static final String SCOPE_2 = "scope-2";
    private static final String USER_ID = "user-456";
    private static final String USER_NAME = "Johnny Walker";
    private static final String USER_EMAIL = "jw@quickcase.app";
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";
    private static final String DEFAULT_JURISDICTION = "org-1";

    private UserInfoExtractor userInfoExtractorStub;
    private AccessTokenAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        userInfoExtractorStub = (claimsParser) -> {
            final String[] authorities = claimsParser.getString("app.quickcase.claims/roles")
                                                     .map((roles) -> roles.split(","))
                                                     .orElseThrow();
            final String defaultJurisdiction = claimsParser.getString("app.quickcase.claims/default_jurisdiction")
                                                           .orElseThrow();
            final UserPreferences preferences = UserPreferences.builder()
                                                               .defaultJurisdiction(defaultJurisdiction)
                                                               .build();
            return UserInfo.builder(claimsParser.getString("sub").orElseThrow())
                    .name(claimsParser.getString("name").orElseThrow())
                    .email(claimsParser.getString("email").orElseThrow())
                    .authorities(authorities)
                    .roles(ROLE_1, ROLE_2)
                    .preferences(preferences)
                    .build();
        };

        converter = new AccessTokenAuthenticationConverter(userInfoExtractorStub);
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
            return userAuthentication(AccessTokenAuthenticationConverter.OPENID_SCOPE);
        }

        private QuickcaseAuthentication userAuthentication(String openidScope) {
            final Jwt jwt = Jwt.withTokenValue(ACCESS_TOKEN)
                               .header("alg", "HS256")
                               .claim("scope", scopes(openidScope, SCOPE_2))
                               .claim("client_id", CLIENT_ID)
                               .claim("sub", USER_ID)
                               .claim("name", USER_NAME)
                               .claim("email", USER_EMAIL)
                               .claim("app.quickcase.claims/roles", roles(ROLE_1, ROLE_2))
                               .claim("app.quickcase.claims/default_jurisdiction", DEFAULT_JURISDICTION)
                               .build();
            return converter.convert(jwt);
        }

        @Test
        @DisplayName("should populate user authentication from access token")
        void shouldGetIdFromUser() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication, instanceOf(QuickcaseUserAuthentication.class));
            assertThat(authentication.getAccessToken(), equalTo(ACCESS_TOKEN));
            assertThat(authentication.getId(), equalTo(USER_ID));
            assertThat(authentication.getName(), equalTo(USER_NAME));
            assertThat(authentication.getEmail().get(), equalTo(USER_EMAIL));
            assertThat(authentication.getAuthorities(), containsInAnyOrder(authorities(AccessTokenAuthenticationConverter.OPENID_SCOPE, SCOPE_2)));
            assertThat(authentication.getRoles(), containsInAnyOrder(ROLE_1, ROLE_2));
            assertThat(authentication.getUserInfo()
                                     .get()
                                     .getPreferences().getDefaultJurisdiction(), equalTo(DEFAULT_JURISDICTION));
        }

        @Test
        @DisplayName("should accept custom scope for openid")
        void shouldAcceptCustomOpenIdScope() {
            converter = new AccessTokenAuthenticationConverter(userInfoExtractorStub, "custom-openid");

            final QuickcaseAuthentication authentication = userAuthentication("custom-openid");

            assertThat(authentication, instanceOf(QuickcaseUserAuthentication.class));
        }
    }

    private String roles(String... items) {
        return String.join(",", items);
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