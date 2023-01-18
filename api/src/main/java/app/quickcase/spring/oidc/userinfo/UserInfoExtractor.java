package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.claims.ClaimsParser;

public interface UserInfoExtractor {
    UserInfo extract(ClaimsParser claimsParser);
}
