package app.quickcase.security.cognito;

import app.quickcase.security.UserInfo;
import app.quickcase.security.authentication.QuickcaseAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@DisplayName("CognitoTokenServices")
@ExtendWith(MockitoExtension.class)
class CognitoTokenServicesTest {
    private static final String ACCESS_TOKEN = "ey123456";
    private static final String SCOPE_OTHER = "resource/other";
    private static final String SCOPE_PROFILE = "profile";
    private static final String CLIENT_ID = "client-123";
    private static final String USER_ID = "user-123";
    private static final String USER_ROLE = "role-123";

    @Mock
    private DefaultTokenServices defaultTokenServices;

    @Mock
    private UserInfoTokenServices userInfoTokenServices;

    @InjectMocks
    private CognitoTokenServices cognitoTokenServices;

    @Nested
    @DisplayName("#loadAuthentication")
    class LoadAuthentication {
        @Test
        @DisplayName("should authenticate client")
        void shouldAuthenticateClient() {
            final OAuth2Authentication clientAuthentication = clientAuthentication(SCOPE_OTHER);
            when(defaultTokenServices.loadAuthentication(ACCESS_TOKEN)).thenReturn(clientAuthentication);

            final OAuth2Authentication auth = cognitoTokenServices.loadAuthentication(ACCESS_TOKEN);

            assertAll(
                    () -> assertThat(auth, instanceOf(QuickcaseAuthentication.class)),
                    () -> assertThat(auth.getAuthorities(), contains(authority(SCOPE_OTHER))),
                    () -> assertThat(((QuickcaseAuthentication) auth).getId(), is(CLIENT_ID))
            );
        }

        @Test
        @DisplayName("should authenticate user")
        void shouldAuthenticateUser() {
            final OAuth2Authentication clientAuthentication = clientAuthentication(SCOPE_OTHER, SCOPE_PROFILE);
            when(defaultTokenServices.loadAuthentication(ACCESS_TOKEN)).thenReturn(clientAuthentication);
            final OAuth2Authentication userAuthentication = userAuthentication();
            when(userInfoTokenServices.loadAuthentication(ACCESS_TOKEN)).thenReturn(userAuthentication);

            final OAuth2Authentication auth = cognitoTokenServices.loadAuthentication(ACCESS_TOKEN);

            assertAll(
                    () -> assertThat(auth, instanceOf(QuickcaseAuthentication.class)),
                    () -> assertThat(auth.getAuthorities(), hasItem(authority(USER_ROLE))),
                    () -> assertThat(((QuickcaseAuthentication) auth).getId(), is(USER_ID))
            );
        }
    }

    @Nested
    @DisplayName("#readAccessToken")
    class ReadAccessToken {
        @Test
        @DisplayName("should read access token from as JWT")
        void shouldReadAsJwt() {
            when(defaultTokenServices.readAccessToken(ACCESS_TOKEN))
                    .thenReturn(new DefaultOAuth2AccessToken(ACCESS_TOKEN));

            final OAuth2AccessToken accessToken = cognitoTokenServices.readAccessToken(ACCESS_TOKEN);

            assertThat(accessToken.getValue(), equalTo(ACCESS_TOKEN));
        }
    }

    private SimpleGrantedAuthority authority(String name) {
        return new SimpleGrantedAuthority(name);
    }

    private OAuth2Authentication clientAuthentication(String... scopes) {
        final OAuth2Request oauth2Request = oauth2Request(scopes);
        return new OAuth2Authentication(oauth2Request, null);
    }

    private OAuth2Authentication userAuthentication() {
        final OAuth2Request oauth2Request = oauth2Request();

        final UserInfo principal = UserInfo.builder().id(USER_ID).authorities(USER_ROLE).build();
        final UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities());

        return new OAuth2Authentication(oauth2Request, userAuth);
    }

    private OAuth2Request oauth2Request(String... scopes) {
        return new OAuth2Request(null,
                                 CLIENT_ID,
                                 null,
                                 false,
                                 new HashSet<>(Arrays.asList(scopes)),
                                 null,
                                 null,
                                 null,
                                 null);
    }
}