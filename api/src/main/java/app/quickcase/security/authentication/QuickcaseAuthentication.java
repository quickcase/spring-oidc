package app.quickcase.security.authentication;

import app.quickcase.security.UserInfo;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * QuickCase-flavoured OAuth 2 authentication. This aims at providing a best-effort in consistency
 * between client-based (client credentials) and user-based (code grant, implicit grant) flows.
 */
public class QuickcaseAuthentication extends OAuth2Authentication {
    static final String DEFAULT_CLIENT_NAME = "System";

    private final Collection<GrantedAuthority> authorities;

    /**
     * Construct a QuickCase-flavoured OAuth 2 authentication.
     * Since some grant types don't require user authentication, the user authentication may be null.
     *
     * @param storedRequest      The authorization request (must not be null).
     * @param userAuthentication The user authentication (possibly null).
     */
    public QuickcaseAuthentication(OAuth2Request storedRequest,
                                   @Nullable Authentication userAuthentication) {
        super(storedRequest, userAuthentication);
        this.authorities = buildAuthorities(storedRequest,userAuthentication);
    }

    /**
     * Unique identifier of the of the request's author.
     *
     * @return subject ID if user authentication is available, client ID otherwise
     */
    public String getId() {
        return getUserInfo().map(UserInfo::getId)
                            .orElse(getOAuth2Request().getClientId());
    }

    /**
     * Display name of the of the request's author.
     *
     * @return subject name if user authentication is available, generic `System` otherwise
     */
    public String getName() {
        return getUserInfo().map(UserInfo::getName)
                            .orElse(DEFAULT_CLIENT_NAME);
    }

    /**
     * Email address of the of the request's author.
     *
     * @return subject email if user authentication is available, empty otherwise
     */
    public Optional<String> getEmail() {
        return getUserInfo().map(UserInfo::getEmail);
    }

    /**
     * User info of the of the request's author.
     *
     * @return subject details if user authentication is available, empty otherwise
     */
    public Optional<UserInfo> getUserInfo() {
        if (!isClientOnly() && UserInfo.class.isAssignableFrom(getPrincipal().getClass()))
            return Optional.ofNullable((UserInfo) getPrincipal());
        return Optional.empty();
    }

    /**
     * All authorities of the request's author.
     *
     * @return Concatenation of client authorities, client scopes and user authorities.
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private List<GrantedAuthority> buildAuthorities(OAuth2Request storedRequest,
                                                    Authentication userAuthentication) {
        final Collection<? extends GrantedAuthority> clientAuthorities = storedRequest.getAuthorities();
        final Collection<? extends GrantedAuthority> userAuthorities = Optional.ofNullable(userAuthentication)
                                                                               .map(Authentication::getAuthorities)
                                                                               .orElse(Collections.emptyList());
        final Collection<? extends GrantedAuthority> clientScopes = scopeAuthorities(storedRequest.getScope());

        validateAuthorities(clientAuthorities);
        validateAuthorities(userAuthorities);

        final int totalSize = clientAuthorities.size() + userAuthorities.size() + clientScopes.size();

        ArrayList<GrantedAuthority> temp = new ArrayList<>(totalSize);
        temp.addAll(clientAuthorities);
        temp.addAll(userAuthorities);
        temp.addAll(clientScopes);

        return Collections.unmodifiableList(temp);
    }

    private Collection<? extends GrantedAuthority> scopeAuthorities(Set<String> scopes) {
        return scopes.stream()
                     .filter(Objects::nonNull)
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toSet());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority a : authorities) {
            if (a == null) {
                throw new IllegalArgumentException(
                        "Authorities collection cannot contain any null elements");
            }
        }
    }
}
