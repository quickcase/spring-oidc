package app.quickcase.spring.oidc.oidc;

import app.quickcase.spring.oidc.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface UserInfoExtractor {
    UserInfo extract(Map<String, JsonNode> claims);
}
