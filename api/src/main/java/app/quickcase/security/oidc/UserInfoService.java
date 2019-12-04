package app.quickcase.security.oidc;

import app.quickcase.security.UserInfo;

public interface UserInfoService {
    UserInfo loadUserInfo(String expectedSubject, String accessToken);
}
