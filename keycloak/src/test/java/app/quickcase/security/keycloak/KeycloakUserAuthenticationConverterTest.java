package app.quickcase.security.keycloak;

import app.quickcase.security.UserAuthenticationToken;
import app.quickcase.security.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.Map;

import static app.quickcase.security.keycloak.KeycloakClaims.APP_ROLES;
import static app.quickcase.security.keycloak.KeycloakClaims.EMAIL;
import static app.quickcase.security.keycloak.KeycloakClaims.NAME;
import static app.quickcase.security.keycloak.KeycloakClaims.SUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

class KeycloakUserAuthenticationConverterTest {
    private static final String USER_ID = "fb09ed5f-bdee-4657-8dcb-891721b63eb2";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@quickcase.app";

    private KeycloakUserAuthenticationConverter userConverter;

    @BeforeEach
    void setUp() {
        userConverter = new KeycloakUserAuthenticationConverter();
    }

    @Test
    @DisplayName("should extract authentication from claims")
    void shouldExtractAuthentication() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, USER_ID);
        claims.put(NAME, USER_NAME);
        claims.put(EMAIL, USER_EMAIL);
        claims.put(APP_ROLES, "role1,role2");

        Authentication authentication = userConverter.extractAuthentication(claims);

        assertThat(authentication.getPrincipal(), instanceOf(UserInfo.class));
        UserInfo principal = (UserInfo) authentication.getPrincipal();

        assertAll(
                () -> assertThat(principal.getId(), equalTo(USER_ID)),
                () -> assertThat(principal.getName(), equalTo(USER_NAME)),
                () -> assertThat(principal.getEmail(), equalTo(USER_EMAIL)),
                () -> assertThat(authentication.getAuthorities(), hasSize(2)),
                () -> assertThat(authentication.getAuthorities(), contains(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                ))
        );
    }

    @Test
    @DisplayName("should convert user to claims")
    void shouldConvertToClaims() {
        UserInfo user = UserInfo.builder()
                                .id(USER_ID)
                                .email(USER_EMAIL)
                                .name(USER_NAME)
                                .authorities("role1", "role2")
                                .build();
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(user);

        Map<String, ?> claims = userConverter.convertUserAuthentication(authenticationToken);

        assertAll(
                () -> assertThat(claims, is(aMapWithSize(4))),
                () -> assertThat(claims.get(SUB), equalTo(USER_ID)),
                () -> assertThat(claims.get(EMAIL), equalTo(USER_EMAIL)),
                () -> assertThat(claims.get(NAME), equalTo(USER_NAME)),
                () -> assertThat(claims.get(APP_ROLES), equalTo("role1,role2"))
        );
    }

}