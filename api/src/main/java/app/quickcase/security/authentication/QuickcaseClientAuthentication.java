package app.quickcase.security.authentication;

import app.quickcase.security.UserInfo;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

public class QuickcaseClientAuthentication extends QuickcaseAuthentication {
    private static final String DEFAULT_NAME = "System";
    private final String clientId;

    public QuickcaseClientAuthentication(String accessToken,
                                         String clientId,
                                         Collection<? extends GrantedAuthority> authorities) {
        super(authorities, accessToken);
        this.clientId = clientId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return getAccessToken();
    }

    @Override
    public Optional<String> getEmail() {
        return Optional.empty();
    }

    @Override
    public String getId() {
        return clientId;
    }

    @Override
    public String getName() {
        return DEFAULT_NAME;
    }

    @Override
    public Object getPrincipal() {
        return getId();
    }

    @Override
    public Optional<UserInfo> getUserInfo() {
        return Optional.empty();
    }

    @Override
    public Boolean isClientOnly() {
        return true;
    }
}
