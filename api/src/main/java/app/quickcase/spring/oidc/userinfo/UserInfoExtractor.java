package app.quickcase.spring.oidc.userinfo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface UserInfoExtractor {
    UserInfo extract(Map<String, JsonNode> claims);
}
