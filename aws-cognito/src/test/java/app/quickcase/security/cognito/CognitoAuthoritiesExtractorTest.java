package app.quickcase.security.cognito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.quickcase.security.cognito.CognitoClaims.APP_ROLES;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("CognitoAuthoritiesExtractor")
class CognitoAuthoritiesExtractorTest {

    private static final String USER_APP_ROLES = "role1,role2";

    @Test
    @DisplayName("should extract authorities from claims")
    void shouldExtractAuthorities() {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(APP_ROLES, USER_APP_ROLES);

        CognitoAuthoritiesExtractor extractor = new CognitoAuthoritiesExtractor();
        List<GrantedAuthority> authorities = extractor.extractAuthorities(claims);

        assertAll(
                () -> assertThat(authorities, hasSize(2)),
                () -> assertThat(authorities, contains(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                ))
        );
    }
}