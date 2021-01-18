package app.quickcase.spring.oidc.authentication;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import app.quickcase.spring.oidc.SecurityClassification;
import app.quickcase.spring.oidc.UserInfo;
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


    private final String id;
    private final String name;
    private final Map<String, OrganisationProfile> organisationProfiles;
    private final UserInfo userInfo;

    @NonNull
    public QuickcaseUserAuthentication(String accessToken,
                                       String id,
                                       String name,
                                       Collection<? extends GrantedAuthority> authorities,
                                       UserInfo userInfo) {
        this(accessToken, id, name, authorities, userInfo, Collections.emptyMap());
    }

    @NonNull
    public QuickcaseUserAuthentication(String accessToken,
                                       String id,
                                       String name,
                                       Collection<? extends GrantedAuthority> authorities,
                                       UserInfo userInfo,
                                       Map<String, OrganisationProfile> organisationProfiles) {
        super(authorities, accessToken);
        this.id = id;
        this.name = name;
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
