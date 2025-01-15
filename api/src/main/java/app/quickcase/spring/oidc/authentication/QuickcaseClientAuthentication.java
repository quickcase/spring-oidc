package app.quickcase.spring.oidc.authentication;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.SecurityClassification;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import org.springframework.security.core.GrantedAuthority;

public class QuickcaseClientAuthentication extends QuickcaseAuthentication {
    private static final String DEFAULT_NAME = "System";
    private static final OrganisationProfile ORGANISATION_PROFILE = clientProfile();

    private static OrganisationProfile clientProfile() {
        return OrganisationProfile.builder()
                                  .accessLevel(AccessLevel.ORGANISATION)
                                  .securityClassification(SecurityClassification.PUBLIC)
                                  .build();
    }

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
    public Set<String> getRoles() {
        return getAuthorities().stream()
                               .map(GrantedAuthority::getAuthority)
                               .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getGroups() {
        return Set.of();
    }

    /**
     * @deprecated Organisation profiles are being phased out in favour of fully role-driven authorisation.
     */
    @Deprecated
    @Override
    public OrganisationProfile getOrganisationProfile(String organisationId) {
        return ORGANISATION_PROFILE;
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
