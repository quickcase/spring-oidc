package app.quickcase.security;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class OrganisationProfile {
    /**
     * Classification of a user for an organisation. Defaults to PUBLIC, the lowest classification.
     */
    private SecurityClassification securityClassification;

    /**
     * Level of access of a user. Defaults to `INDIVIDUAL`, the most restrictive level.
     */
    private AccessLevel accessLevel;

    /**
     * Optional. For users with access level `GROUP` only: the group the user belongs to for the
     * organisation.
     */
    private String group;

    public Optional<String> getGroup() {
        return Optional.ofNullable(group);
    }

    public static class OrganisationProfileBuilder {
        private SecurityClassification securityClassification = SecurityClassification.PUBLIC;
        private AccessLevel accessLevel = AccessLevel.INDIVIDUAL;

        public OrganisationProfileBuilder securityClassification(SecurityClassification classification) {
            if (null != classification)
                this.securityClassification = classification;
            return this;
        }

        public OrganisationProfileBuilder accessLevel(AccessLevel accessLevel) {
            if (null != accessLevel)
                this.accessLevel = accessLevel;
            return this;
        }
    }
}
