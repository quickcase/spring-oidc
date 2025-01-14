package app.quickcase.spring.oidc.authentication.converter;

import java.util.Set;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthentication;
import app.quickcase.spring.oidc.authentication.QuickcaseClientAuthentication;
import app.quickcase.spring.oidc.authentication.QuickcaseUserAuthentication;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import app.quickcase.spring.oidc.userinfo.UserInfoService;

import org.springframework.security.oauth2.jwt.Jwt;

import static app.quickcase.spring.oidc.utils.StringUtils.authorities;
import static app.quickcase.spring.oidc.utils.StringUtils.fromSpaceSeparated;

public class UserInfoAuthenticationConverter implements QuickcaseAuthenticationConverter {
    public static final String OPENID_SCOPE = "openid";

    private final UserInfoService userInfoService;
    private final String openidScope;

    public UserInfoAuthenticationConverter(UserInfoService userInfoService) {
        this(userInfoService, OPENID_SCOPE);
    }

    public UserInfoAuthenticationConverter(UserInfoService userInfoService, String openidScope) {
        this.userInfoService = userInfoService;
        this.openidScope = openidScope;
    }

    @Override
    public QuickcaseAuthentication convert(Jwt source) {
        final Set<String> scopes = fromSpaceSeparated(source.getClaimAsString("scope"));

        if (scopes.contains(openidScope)) {
            return userAuthentication(source);
        }

        return clientAuthentication(source);
    }

    private QuickcaseAuthentication clientAuthentication(Jwt source) {
        final String accessToken = source.getTokenValue();
        final String subject = source.getSubject();
        final Set<String> scopes = fromSpaceSeparated(source.getClaimAsString("scope"));
        return new QuickcaseClientAuthentication(accessToken, subject, authorities(scopes));
    }

    private QuickcaseUserAuthentication userAuthentication(Jwt source) {
        final String subject = source.getSubject();
        final String accessToken = source.getTokenValue();
        final Set<String> scopes = fromSpaceSeparated(source.getClaimAsString("scope"));
        final UserInfo userInfo = userInfoService.loadUserInfo(subject, accessToken);

        return new QuickcaseUserAuthentication(accessToken, authorities(scopes), userInfo);
    }
}
