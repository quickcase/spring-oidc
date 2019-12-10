package app.quickcase.security.authentication;

import app.quickcase.security.UserInfo;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

public class QuickcaseUserAuthentication extends QuickcaseAuthentication {
    private final String id;
    private final String name;
    private final UserInfo userInfo;

    @NonNull
    public QuickcaseUserAuthentication(String accessToken,
                                       String id,
                                       String name,
                                       Collection<? extends GrantedAuthority> authorities,
                                       UserInfo userInfo) {
        super(authorities, accessToken);
        this.setAuthenticated(true);
        this.id = id;
        this.name = name;
        this.userInfo = userInfo;
    }

    @Override
    public Object getCredentials() {
        return getAccessToken();
    }

    @Override
    public Optional<String> getEmail() {
        return getUserInfo().map(UserInfo::getEmail);
    }

    @Override
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

    @Override
    public Optional<UserInfo> getUserInfo() {
        return Optional.of(userInfo);
    }

    @Override
    public Boolean isClientOnly() {
        return false;
    }
}
