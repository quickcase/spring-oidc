package app.quickcase.spring.oidc.authentication;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.SecurityClassification;
import app.quickcase.spring.oidc.userinfo.UserInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Slf4j
public class QuickcaseUserAuthentication extends QuickcaseAuthentication {
    public static final OrganisationProfile DEFAULT_PROFILE = defaultProfile();
    private static OrganisationProfile defaultProfile() {
        return OrganisationProfile.builder()
                                  .accessLevel(AccessLevel.INDIVIDUAL)
                                  .securityClassification(SecurityClassification.PUBLIC)
                                  .build();
    }

    private final Map<String, OrganisationProfile> organisationProfiles;
    private final UserInfo userInfo;

    @NonNull
    public QuickcaseUserAuthentication(@NonNull String accessToken,
                                       @NonNull Collection<? extends GrantedAuthority> authorities,
                                       @NonNull UserInfo userInfo) {
        this(accessToken, authorities, userInfo, Collections.emptyMap());
    }

    @NonNull
    public QuickcaseUserAuthentication(@NonNull String accessToken,
                                       @NonNull Collection<? extends GrantedAuthority> authorities,
                                       @NonNull UserInfo userInfo,
                                       @NonNull Map<String, OrganisationProfile> organisationProfiles) {
        super(authorities, accessToken);
        this.userInfo = userInfo;
        this.organisationProfiles = caseInsensitiveMap(organisationProfiles);
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
    public OrganisationProfile getOrganisationProfile(String organisationId) {
        return Optional.ofNullable(organisationProfiles.get(organisationId))
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

    private Map<String, OrganisationProfile> caseInsensitiveMap(
            Map<String, OrganisationProfile> sourceMap) {
        final TreeMap<String, OrganisationProfile> treeMap = new TreeMap<>(String::compareToIgnoreCase);
        treeMap.putAll(sourceMap);
        return treeMap;
    }
}
