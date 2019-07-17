package app.quickcase.security.utils;

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
class AuthoritiesUtilsTest {

    @Test
    @DisplayName("should extract set of authorities from comma-separated string")
    void shouldExtractFromCommaSeparated() {
        Set<GrantedAuthority> authorities = AuthoritiesUtils.fromCommaSeparated("role1,role2");

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
        String authoritiesString = AuthoritiesUtils.toCommaSeparated(authorities);

        assertThat(authoritiesString, equalTo("role1,role2"));
    }

}