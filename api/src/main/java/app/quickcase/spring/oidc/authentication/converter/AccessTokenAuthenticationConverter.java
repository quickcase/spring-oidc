package app.quickcase.spring.oidc.authentication.converter;

import java.util.Set;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthentication;
import app.quickcase.spring.oidc.authentication.QuickcaseClientAuthentication;
import app.quickcase.spring.oidc.authentication.QuickcaseUserAuthentication;
import app.quickcase.spring.oidc.claims.ClaimsParser;
import app.quickcase.spring.oidc.claims.JwtClaimsParser;
import app.quickcase.spring.oidc.userinfo.UserInfoExtractor;

import org.springframework.security.oauth2.jwt.Jwt;

import static app.quickcase.spring.oidc.utils.StringUtils.authorities;
import static app.quickcase.spring.oidc.utils.StringUtils.fromSpaceSeparated;

/**
 * Extract all QuickCase user claims from the access token. Does not call /userinfo endpoint.
 * For client authentication, follows the behaviour of {@link UserInfoAuthenticationConverter}.
 */
public class AccessTokenAuthenticationConverter implements QuickcaseAuthenticationConverter {
    public static final String OPENID_SCOPE = "openid";

    private final UserInfoExtractor userInfoExtractor;
    private final String openidScope;

    public AccessTokenAuthenticationConverter(UserInfoExtractor userInfoExtractor) {
        this(userInfoExtractor, OPENID_SCOPE);
    }

    public AccessTokenAuthenticationConverter(UserInfoExtractor userInfoExtractor, String openidScope) {
        this.userInfoExtractor = userInfoExtractor;
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

    private QuickcaseAuthentication userAuthentication(Jwt source) {
        final ClaimsParser claims = new JwtClaimsParser(source.getClaims());
        final Set<String> scopes = fromSpaceSeparated(source.getClaimAsString("scope"));
        return new QuickcaseUserAuthentication(source.getTokenValue(), authorities(scopes), userInfoExtractor.extract(claims));
    }
}
