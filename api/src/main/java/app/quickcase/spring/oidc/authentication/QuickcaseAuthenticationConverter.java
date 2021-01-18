package app.quickcase.spring.oidc.authentication;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.UserInfo;
import app.quickcase.spring.oidc.oidc.UserInfoService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.Set;

import static app.quickcase.spring.oidc.utils.StringUtils.authorities;
import static app.quickcase.spring.oidc.utils.StringUtils.fromSpaceSeparated;

public class QuickcaseAuthenticationConverter implements Converter<Jwt, QuickcaseAuthentication> {
    private static final String DEFAULT_PROFILE_SCOPE = "profile";

    private final UserInfoService userInfoService;
    private String profileScope;

    public QuickcaseAuthenticationConverter(UserInfoService userInfoService) {
        this(userInfoService, DEFAULT_PROFILE_SCOPE);
    }

    public QuickcaseAuthenticationConverter(UserInfoService userInfoService, String profileScope) {
        this.userInfoService = userInfoService;
        this.profileScope = profileScope;
    }

    @Override
    public QuickcaseAuthentication convert(Jwt source) {
        final Set<String> scopes = fromSpaceSeparated(source.getClaimAsString("scope"));

        if (scopes.contains(profileScope)) {
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
        final String name = userInfo.getName();
        final Set<GrantedAuthority> authorities = userInfo.getAuthorities();
        final Map<String, OrganisationProfile> orgProfiles = userInfo.getOrganisationProfiles();

        return new QuickcaseUserAuthentication(accessToken,
                                               subject,
                                               name,
                                               authorities,
                                               userInfo,
                                               orgProfiles);
    }
}
