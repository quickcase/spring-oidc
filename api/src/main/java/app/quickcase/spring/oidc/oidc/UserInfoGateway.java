package app.quickcase.spring.oidc.oidc;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface UserInfoGateway {
    Map<String, JsonNode> getClaims(String accessToken);
}
