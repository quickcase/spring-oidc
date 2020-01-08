package app.quickcase.security.oidc;

import app.quickcase.security.UserInfo;

import java.util.Map;

public interface UserInfoExtractor {
    UserInfo extract(Map<String, Object> claims);
}
