package app.quickcase.security.cognito;

import app.quickcase.security.OrganisationProfile;
import app.quickcase.security.UserInfo;
import app.quickcase.security.authentication.QuickcaseAuthentication;
import app.quickcase.security.authentication.QuickcaseClientAuthentication;
import app.quickcase.security.authentication.QuickcaseUserAuthentication;
import app.quickcase.security.oidc.UserInfoService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.Set;

import static app.quickcase.security.utils.StringUtils.authorities;
import static app.quickcase.security.utils.StringUtils.fromSpaceSeparated;

public class CognitoAuthenticationConverter implements Converter<Jwt, QuickcaseAuthentication> {
    private static final String SCOPE_PROFILE = "profile";

    private final UserInfoService userInfoService;

    public CognitoAuthenticationConverter(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public QuickcaseAuthentication convert(Jwt source) {
        final Set<String> scopes = fromSpaceSeparated(source.getClaimAsString("scope"));

        if (scopes.contains(SCOPE_PROFILE)) {
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
