package app.quickcase.security.cognito;

import app.quickcase.security.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.Map;

import static app.quickcase.security.cognito.CognitoClaims.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("CognitoPrincipalExtractor")
class CognitoPrincipalExtractorTest {
    private static final String USER_ID = "eec55037-bac7-46b4-9849-f063e627e4f3";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@quickcase.app";
    private static final String USER_APP_ROLES = "role1,role2";
    private static final String USER_JURISDICTIONS = "jid1,jid2";

    @Test
    @DisplayName("should extract principal from claims")
    void shouldExtractPrincipal() {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, USER_ID);
        claims.put(NAME, USER_NAME);
        claims.put(EMAIL, USER_EMAIL);
        claims.put(APP_ROLES, USER_APP_ROLES);
        claims.put(APP_JURISDICTIONS, USER_JURISDICTIONS);

        CognitoPrincipalExtractor extractor = new CognitoPrincipalExtractor();

        Object principal = extractor.extractPrincipal(claims);

        assertThat(principal, instanceOf(UserInfo.class));

        UserInfo userInfo = (UserInfo) principal;

        assertAll(
                () -> assertThat(userInfo.getId(), equalTo(USER_ID)),
                () -> assertThat(userInfo.getName(), equalTo(USER_NAME)),
                () -> assertThat(userInfo.getEmail(), equalTo(USER_EMAIL)),
                () -> assertThat(userInfo.getAuthorities(), containsInAnyOrder(
                        new SimpleGrantedAuthority("role1"),
                        new SimpleGrantedAuthority("role2")
                )),
                () -> assertThat(userInfo.getJurisdictions(),
                                 containsInAnyOrder("jid1", "jid2"))
        );
    }

}