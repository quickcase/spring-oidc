package app.quickcase.security.oidc;

import app.quickcase.security.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("DefaultUserInfoService")
class DefaultUserInfoServiceTest {
    private static final String ACCESS_TOKEN = "accessToken9080";
    private static final String SUBJECT = "user-51423";

    private final UserInfoGateway stubGateway = accessToken -> {
        final HashMap<String, JsonNode> claims = new HashMap<>();
        claims.put("sub", getTextNode(SUBJECT));
        claims.put("name", getTextNode(accessToken));
        return claims;
    };

    private final UserInfoExtractor stubExtractor = claims -> UserInfo.builder()
                                                                      .id(claims.get("sub").textValue())
                                                                      .name(claims.get("name").textValue())
                                                                      .build();

    @Test
    @DisplayName("should fetch and extract user info")
    void shouldFetchAndExtractUserInfo() {
        final DefaultUserInfoService userInfoService = new DefaultUserInfoService(stubGateway,
                                                                                  stubExtractor);

        final UserInfo userInfo = userInfoService.loadUserInfo(SUBJECT, ACCESS_TOKEN);

        assertThat(userInfo.getId(), equalTo(SUBJECT));
        assertThat(userInfo.getName(), equalTo(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("should fail when expected subject does not match user info subject")
    void shouldFailOnSubjectMismatch() {
        final DefaultUserInfoService userInfoService = new DefaultUserInfoService(stubGateway,
                                                                                  stubExtractor);

        assertThrows(OidcException.class,
                     () -> userInfoService.loadUserInfo("other", ACCESS_TOKEN));
    }

    private JsonNode getTextNode(String value) {
        return new TextNode(value);
    }
}
