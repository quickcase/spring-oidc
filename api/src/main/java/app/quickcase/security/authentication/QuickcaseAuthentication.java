package app.quickcase.security.authentication;

import app.quickcase.security.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

/**
 * QuickCase-flavoured authentication. This aims at providing a best-effort in consistency
 * between client-based (client credentials) and user-based (code grant, implicit grant) flows.
 */
public class QuickcaseAuthentication extends AbstractAuthenticationToken {

    private final String accessToken;
    private final String id;
    private final String name;
    private final UserInfo userInfo;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public QuickcaseAuthentication(String accessToken, String id, String name, Collection<? extends GrantedAuthority> authorities) {
        this(accessToken, id, name, authorities, null);
    }

    public QuickcaseAuthentication(String accessToken, String id, String name, Collection<? extends GrantedAuthority> authorities, UserInfo userInfo) {
        super(authorities);
        this.setAuthenticated(true);
        this.accessToken = accessToken;
        this.id = id;
        this.name = name;
        this.userInfo = userInfo;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Object getCredentials() {
        return getAccessToken();
    }

    public Optional<String> getEmail() {
        return getUserInfo().map(UserInfo::getEmail);
    }

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getPrincipal() {
        return getId();
    }

    public Optional<UserInfo> getUserInfo() {
        return Optional.ofNullable(userInfo);
    }

    public Boolean isClientOnly() {
        return !getUserInfo().isPresent();
    }
}
