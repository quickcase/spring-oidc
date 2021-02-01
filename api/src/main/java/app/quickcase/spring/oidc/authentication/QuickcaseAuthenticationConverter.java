package app.quickcase.spring.oidc.authentication;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import app.quickcase.spring.oidc.userinfo.UserInfoService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.Set;

import static app.quickcase.spring.oidc.utils.StringUtils.authorities;
import static app.quickcase.spring.oidc.utils.StringUtils.fromSpaceSeparated;

public class QuickcaseAuthenticationConverter implements Converter<Jwt, QuickcaseAuthentication> {
    public static final String OPENID_SCOPE = "openid";

    private final UserInfoService userInfoService;
    private final String openidScope;

    public QuickcaseAuthenticationConverter(UserInfoService userInfoService) {
        this(userInfoService, OPENID_SCOPE);
    }

    public QuickcaseAuthenticationConverter(UserInfoService userInfoService, String openidScope) {
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
        final UserInfo userInfo = userInfoService.loadUserInfo(subject, accessToken);
        final Set<GrantedAuthority> authorities = userInfo.getAuthorities();
        final Map<String, OrganisationProfile> orgProfiles = userInfo.getOrganisationProfiles();

        return new QuickcaseUserAuthentication(accessToken,
                                               authorities,
                                               userInfo,
                                               orgProfiles);
    }
}
