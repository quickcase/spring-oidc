package app.quickcase.security.authentication;

import app.quickcase.security.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static app.quickcase.security.authentication.QuickcaseAuthentication.DEFAULT_CLIENT_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("QuickcaseAuthentication")
class QuickcaseAuthenticationTest {
    private static final String CLIENT_ID = "client-123";
    private static final String SCOPE_1 = "resource/scope1";
    private static final String SCOPE_2 = "resource/scope2";
    private static final String USER_ID = "subject-123";
    private static final String USER_NAME = "Jane Doe";
    private static final String USER_EMAIL = "jane.doe@quickcase.app";
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";
    private static final String JURISDICTION_1 = "juris-1";
    private static final String JURISDICTION_2 = "juris-2";

    @Nested
    @DisplayName("when client authentication")
    class WhenClientAuthentication {
        private QuickcaseAuthentication clientAuthentication() {
            final OAuth2Request request = oauth2Request();
            return new QuickcaseAuthentication(request, null);
        }

        @Test
        @DisplayName("should get ID from client")
        void shouldGetIdFromClient() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.getId(), equalTo(CLIENT_ID));
        }

        @Test
        @DisplayName("should use default name")
        void shouldUseDefaultName() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.getName(), equalTo(DEFAULT_CLIENT_NAME));
        }

        @Test
        @DisplayName("should not have email")
        void shouldNotHaveEmail() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.getEmail().isPresent(), is(false));
        }

        @Test
        @DisplayName("should not have user info")
        void shouldNotHaveUserInfo() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.getUserInfo().isPresent(), is(false));
        }

        @Test
        @DisplayName("should use scopes as authorities")
        void shouldUseScopesAsAuthorities() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            final GrantedAuthority[] scopes = authorities(SCOPE_1, SCOPE_2);
            assertThat(authentication.getAuthorities(), containsInAnyOrder(scopes));
        }

        @Test
        @DisplayName("should be client only")
        void shouldBeClientOnly() {
            final QuickcaseAuthentication authentication = clientAuthentication();

            assertThat(authentication.isClientOnly(), is(true));
        }
    }

    @Nested
    @DisplayName("when user authentication")
    class WhenUserAuthentication {
        private QuickcaseAuthentication userAuthentication() {
            final OAuth2Request request = oauth2Request();
            final UserInfo principal = UserInfo.builder()
                                               .id(USER_ID)
                                               .name(USER_NAME)
                                               .email(USER_EMAIL)
                                               .authorities(ROLE_1, ROLE_2)
                                               .jurisdictions(JURISDICTION_1, JURISDICTION_2)
//                                               .preferences()
                                               .build();
            final Authentication userAuthentication = new UsernamePasswordAuthenticationToken(principal, "N/A", principal.getAuthorities());
            return new QuickcaseAuthentication(request, userAuthentication);
        }

        @Test
        @DisplayName("should get ID from subject")
        void shouldGetIdFromClient() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getId(), equalTo(USER_ID));
        }

        @Test
        @DisplayName("should use subject name")
        void shouldUseDefaultName() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getName(), equalTo(USER_NAME));
        }

        @Test
        @DisplayName("should use subject email")
        void shouldNotHaveEmail() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getEmail().get(), equalTo(USER_EMAIL));
        }

        @Test
        @DisplayName("should have user info")
        void shouldNotHaveUserInfo() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.getUserInfo().get().getEmail(), equalTo(USER_EMAIL));
        }

        @Test
        @DisplayName("should combine user and client authorities")
        void shouldUseScopesAsAuthorities() {
            final QuickcaseAuthentication authentication = userAuthentication();

            final GrantedAuthority[] allAuthorities = authorities(SCOPE_1, SCOPE_2, ROLE_1, ROLE_2);
            assertThat(authentication.getAuthorities(), containsInAnyOrder(allAuthorities));
        }

        @Test
        @DisplayName("should not be client only")
        void shouldBeClientOnly() {
            final QuickcaseAuthentication authentication = userAuthentication();

            assertThat(authentication.isClientOnly(), is(false));
        }
    }

    private OAuth2Request oauth2Request() {
        final Set<String> scopes = new HashSet<>(Arrays.asList(SCOPE_1, SCOPE_2));
        return new OAuth2Request(null, CLIENT_ID, null, false, scopes, null, null, null, null);
    }

    private GrantedAuthority[] authorities(String ...authorities) {
        return Arrays.stream(authorities)
                     .map(SimpleGrantedAuthority::new)
                     .toArray(GrantedAuthority[]::new);
    }
}