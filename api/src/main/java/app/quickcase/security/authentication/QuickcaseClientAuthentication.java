package app.quickcase.security.authentication;

import app.quickcase.security.AccessLevel;
import app.quickcase.security.organisation.OrganisationProfile;
import app.quickcase.security.SecurityClassification;
import app.quickcase.security.UserInfo;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

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
