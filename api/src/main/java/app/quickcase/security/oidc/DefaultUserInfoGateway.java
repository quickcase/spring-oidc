package app.quickcase.security.oidc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

public class DefaultUserInfoGateway implements UserInfoGateway {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final URI userInfoUri;
    private final RestTemplate restTemplate;

    public DefaultUserInfoGateway(URI userInfoUri, RestTemplate restTemplate) {
        this.userInfoUri = userInfoUri;
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, JsonNode> getClaims(String accessToken) {
        final HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders(accessToken));
        final ResponseEntity<JsonNode> response = restTemplate.exchange(userInfoUri,
                                                                        HttpMethod.GET,
                                                                        requestEntity,
                                                                        JsonNode.class);

        return MAPPER.convertValue(response.getBody(), new TypeReference<Map<String, JsonNode>>(){});
    }

    private HttpHeaders createHeaders(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return headers;
    }
}
