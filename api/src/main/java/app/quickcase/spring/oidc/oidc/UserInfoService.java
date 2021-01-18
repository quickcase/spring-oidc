package app.quickcase.spring.oidc.oidc;

import app.quickcase.spring.oidc.UserInfo;

public interface UserInfoService {
    UserInfo loadUserInfo(String expectedSubject, String accessToken);
}
