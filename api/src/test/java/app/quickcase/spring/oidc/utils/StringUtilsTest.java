package app.quickcase.spring.oidc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("AuthoritiesUtils")
class StringUtilsTest {

    @Test
    @DisplayName("should extract set of authorities from comma-separated string")
    void shouldExtractFromCommaSeparated() {
        Set<GrantedAuthority> authorities = StringUtils.fromCommaSeparated("role1,role2");

        assertAll(
                () -> assertThat(authorities, hasSize(2)),
                () -> assertThat(authorities, contains(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                ))
        );
    }

    @Test
    @DisplayName("should convert set of authorities to comma-separated string")
    void shouldConvertToCommaSeparated() {
        HashSet<GrantedAuthority> authorities = new HashSet<>(
                Arrays.asList(new SimpleGrantedAuthority("role1"),
                              new SimpleGrantedAuthority("role2"))
        );
        String authoritiesString = StringUtils.toCommaSeparated(authorities);

        assertThat(authoritiesString, equalTo("role1,role2"));
    }

    @Test
    @DisplayName("should extract set of strings from space-separated string")
    void shouldExtractFromSpaceSeparated() {
        Set<String> roles = StringUtils.fromSpaceSeparated("role1 role2");

        assertAll(
                () -> assertThat(roles, hasSize(2)),
                () -> assertThat(roles, contains("role1", "role2"))
        );
    }

    @Test
    @DisplayName("should convert set of strings to authorities")
    void shouldConvertToAuthorities() {
        final Set<String> strings = new HashSet<>(Arrays.asList("autho1", "autho2"));
        final Set<GrantedAuthority> authorities = StringUtils.authorities(strings);

        assertAll(
                () -> assertThat(authorities, hasSize(2)),
                () -> assertThat(authorities, containsInAnyOrder(
                        new SimpleGrantedAuthority("autho1"),
                        new SimpleGrantedAuthority("autho2")
                ))
        );
    }

}