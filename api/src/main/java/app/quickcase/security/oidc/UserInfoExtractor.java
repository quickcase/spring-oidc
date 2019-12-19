package app.quickcase.security.oidc;

import app.quickcase.security.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface UserInfoExtractor {
    UserInfo extract(Map<String, JsonNode> claims);
}
