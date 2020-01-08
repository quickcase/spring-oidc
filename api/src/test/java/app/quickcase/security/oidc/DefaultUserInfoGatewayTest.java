package app.quickcase.security.oidc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultUserInfoGateway")
class DefaultUserInfoGatewayTest {
    private static final URI USER_INFO_URI = URI.create("https://oidc.local/userInfo");
    private static final String ACCESS_TOKEN = "access6789";

    @Mock
    private RestTemplate restTemplate;

    private DefaultUserInfoGateway userInfoGateway;

    @BeforeEach
    void setUp() {
        userInfoGateway = new DefaultUserInfoGateway(USER_INFO_URI, restTemplate);
    }

    @Test
    void shouldGetUserInfo() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode responseBody = objectMapper.createObjectNode();
        responseBody.set("sub", objectMapper.convertValue("user-123", JsonNode.class));
        final ResponseEntity<JsonNode> response = ResponseEntity.ok(responseBody);
        when(restTemplate.exchange(eq(USER_INFO_URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(response);

        final Map<String, Object> claims = userInfoGateway.getClaims(ACCESS_TOKEN);

        assertAll(
                () -> assertThat(claims.entrySet(), hasSize(1)),
                () -> assertThat(claims.get("sub"), equalTo("user-123"))
        );
    }
}
