package app.quickcase.security.oidc;

import java.util.Map;

public interface UserInfoGateway {
    Map<String, Object> getClaims(String accessToken);
}
