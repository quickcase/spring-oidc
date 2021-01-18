package app.quickcase.spring.oidc.userinfo;

public interface UserInfoService {
    UserInfo loadUserInfo(String expectedSubject, String accessToken);
}
