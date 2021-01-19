package app.quickcase.spring.oidc.userinfo;

import app.quickcase.spring.oidc.UserAuthenticationToken;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.*;

/**
 * Provides QuickCase user information.
 *
 * <p>
 * Hold non-security related user information for QuickCase users such as name and email.
 *
 * <p>
 * Encapsulated in a {@link UserAuthenticationToken} to link with Spring Security.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Value
@Builder
@ToString(onlyExplicitlyIncluded = true) // GDPR: Keep names and emails outside of logs
public class UserInfo implements Principal, UserDetails {
    @ToString.Include
    private final String id;
    private final String name;
    private final String email;
    @ToString.Include
    private final Set<GrantedAuthority> authorities;
    private final UserPreferences preferences;
    private final Map<String, OrganisationProfile> organisationProfiles;

    @Override
    public String getPassword() {
        return "N/A";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @deprecated To be removed in v2.0.0, {@link #getOrganisationProfiles()} should be used instead.
     * @return Set of organisation IDs, extracted from organisation profiles.
     */
    @Deprecated
    public Set<String> getJurisdictions() {
        return Optional.ofNullable(organisationProfiles)
                       .map(Map::keySet)
                       .orElse(Collections.emptySet());
    }

    public static class UserInfoBuilder {
        private Set<GrantedAuthority> authorities = new HashSet<>();
        private Map<String, OrganisationProfile> organisationProfiles = new HashMap<>();

        public UserInfoBuilder authorities(Set<GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public UserInfoBuilder authorities(String... authorities) {
            Arrays.stream(authorities)
                  .map(SimpleGrantedAuthority::new)
                  .forEach(this.authorities::add);
            return this;
        }

        public UserInfoBuilder organisationProfile(String identifier, OrganisationProfile profile) {
            this.organisationProfiles.put(identifier, profile);
            return this;
        }

        public UserInfoBuilder organisationProfiles(Map<String, OrganisationProfile> profiles) {
            this.organisationProfiles.putAll(profiles);
            return this;
        }
    }
}
