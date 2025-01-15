package app.quickcase.spring.oidc.authentication;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * QuickCase-flavoured authentication. This aims at providing a best-effort in consistency
 * between client-based (client credentials) and user-based (code grant, implicit grant) flows.
 */
public abstract class QuickcaseAuthentication extends AbstractAuthenticationToken {

    private final String accessToken;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     * @param accessToken the OAuth2 access token supporting the authentication.
     */
    public QuickcaseAuthentication(Collection<? extends GrantedAuthority> authorities,
                                   String accessToken) {
        super(authorities);
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public abstract Optional<String> getEmail();

    public abstract String getId();

    public abstract Set<String> getRoles();

    public abstract Set<String> getGroups();

    /**
     * @deprecated Organisation profiles are being phased out in favour of fully role-driven authorisation.
     */
    @Deprecated
    public abstract OrganisationProfile getOrganisationProfile(String organisationId);

    public abstract Optional<UserInfo> getUserInfo();

    public abstract Boolean isClientOnly();
}
