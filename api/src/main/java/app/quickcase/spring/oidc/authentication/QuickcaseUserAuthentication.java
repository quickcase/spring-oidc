package app.quickcase.spring.oidc.authentication;

import java.util.Optional;
import java.util.Set;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.SecurityClassification;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickcaseUserAuthentication extends QuickcaseAuthentication {
    public static final OrganisationProfile DEFAULT_PROFILE = defaultProfile();
    private static OrganisationProfile defaultProfile() {
        return OrganisationProfile.builder()
                                  .accessLevel(AccessLevel.INDIVIDUAL)
                                  .securityClassification(SecurityClassification.PUBLIC)
                                  .build();
    }

    private final UserInfo userInfo;

    public QuickcaseUserAuthentication(@NonNull String accessToken, @NonNull UserInfo userInfo) {
        super(userInfo.getAuthorities(), accessToken);
        this.userInfo = userInfo;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return getAccessToken();
    }

    @Override
    public Optional<String> getEmail() {
        return userInfo.getEmail();
    }

    @Override
    public String getId() {
        return userInfo.getSubject();
    }

    @Override
    public String getName() {
        return userInfo.getName();
    }

    @Override
    public Set<String> getGroups() {
        return userInfo.getGroups();
    }

    @Override
    public OrganisationProfile getOrganisationProfile(String organisationId) {
        return Optional.ofNullable(userInfo.getOrganisationProfiles().get(organisationId))
                       .orElseGet(() -> {
                           log.debug(
                                   "No profile found for user `{}` and organisation `{}`, " +
                                           "defaulting to PUBLIC/INDIVIDUAL",
                                   getId(),
                                   organisationId);
                           return DEFAULT_PROFILE;
                       });
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
