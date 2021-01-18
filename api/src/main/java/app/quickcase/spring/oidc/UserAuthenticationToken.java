package app.quickcase.spring.oidc;

import app.quickcase.spring.oidc.userinfo.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Encapsulate an authenticated QuickCase user.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final UserInfo principal;

    public UserAuthenticationToken(UserInfo user) {
        super(user.getAuthorities());
        this.principal = user;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return principal.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor instead");
        }

        super.setAuthenticated(false);
    }
}
