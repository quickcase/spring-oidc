package app.quickcase.spring.oidc.userinfo;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import app.quickcase.spring.oidc.UserAuthenticationToken;
import app.quickcase.spring.oidc.organisation.OrganisationProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true) // GDPR: Keep names and emails outside of logs
public class UserInfo implements Principal, UserDetails {
    @NonNull
    @ToString.Include
    private final String subject;
    private final String name;
    private final String email;
    @NonNull
    @ToString.Include
    private final Set<GrantedAuthority> authorities;
    @NonNull
    @ToString.Include
    private final Set<String> roles;
    @NonNull
    @ToString.Include
    private final Set<String> groups;
    private final UserPreferences preferences;

    /**
     * @deprecated Organisation profiles are being phased out in favour of fully role-driven authorisation.
     */
    @Deprecated
    @NonNull
    private final Map<String, OrganisationProfile> organisationProfiles;

    public static UserInfoBuilder builder(String subject) {
        return new UserInfoBuilder(subject);
    }

    @Override
    public String getPassword() {
        return "N/A";
    }

    @Override
    public String getUsername() {
        return email != null ? email : subject;
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

    public String getName() {
        return name != null ? name : subject;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    @RequiredArgsConstructor
    public static class UserInfoBuilder {
        private final String subject;
        private String name;
        private String email;
        private Set<GrantedAuthority> authorities = new HashSet<>();
        private Set<String> roles = new HashSet<>();
        private Set<String> groups = new HashSet<>();
        private UserPreferences preferences;
        private final Map<String, OrganisationProfile> organisationProfiles = new TreeMap<>(String::compareToIgnoreCase);

        public UserInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserInfoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserInfoBuilder authorities(Set<GrantedAuthority> authorities) {
            this.authorities.addAll(authorities);
            return this;
        }

        public UserInfoBuilder authorities(String... authorities) {
            Arrays.stream(authorities)
                  .map(SimpleGrantedAuthority::new)
                  .forEach(this.authorities::add);
            return this;
        }

        public UserInfoBuilder roles(Set<String> roles) {
            this.roles.addAll(roles);
            return this;
        }

        public UserInfoBuilder roles(String... roles) {
            this.roles.addAll(Arrays.asList(roles));
            return this;
        }

        public UserInfoBuilder groups(Set<String> groups) {
            this.groups.addAll(groups);
            return this;
        }

        public UserInfoBuilder groups(String... groups) {
            this.groups.addAll(Arrays.asList(groups));
            return this;
        }

        public UserInfoBuilder preferences(UserPreferences preferences) {
            this.preferences = preferences;
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

        public UserInfo build() {
            return new UserInfo(subject, name, email, authorities, roles, groups, preferences, organisationProfiles);
        }
    }
}
