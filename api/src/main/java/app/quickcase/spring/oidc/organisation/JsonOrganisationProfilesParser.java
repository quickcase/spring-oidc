package app.quickcase.spring.oidc.organisation;

import app.quickcase.spring.oidc.AccessLevel;
import app.quickcase.spring.oidc.SecurityClassification;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Parse JSON objects like:
 * <pre>
 *     {
 *         "{anOrgId}": {
 *             "access": "INDIVIDUAL",
 *             "classification": "PUBLIC",
 *             "group": "optional"
 *         }
 *     }
 * </pre>
 */
@Slf4j
public class JsonOrganisationProfilesParser implements OrganisationProfilesParser<JsonNode> {
    private static final String NODE_ACCESS = "access";
    private static final String NODE_CLASSIFICATION = "classification";
    private static final String NODE_GROUP = "group";

    @Override
    public Map<String, OrganisationProfile> parse(JsonNode tree) {
        if (tree == null || !tree.isObject()) {
            log.warn("Failed to parse organisations: expected object but was {}", tree);
            return Collections.emptyMap();
        }

        final Map<String, OrganisationProfile> profiles = new HashMap<>();
        tree.fieldNames()
            .forEachRemaining(orgId -> toOrganisationProfile(orgId, tree.get(orgId))
                    .ifPresent(profile -> profiles.put(orgId, profile)));

        log.debug("Parsed {} organisation profiles", profiles.size());
        return profiles;
    }

    private Optional<OrganisationProfile> toOrganisationProfile(String orgId, JsonNode node) {
        if (node == null || !node.isObject()) {
            log.warn("Failed to parse organisation `{}`: expected object but was {}", orgId, node);
            return Optional.empty();
        }

        final OrganisationProfile.OrganisationProfileBuilder builder = OrganisationProfile.builder();

        extractAccessLevel(node)
                .ifPresent(builder::accessLevel);
        extractSecurityClassification(node)
                .ifPresent(builder::securityClassification);
        extractGroup(node)
                .ifPresent(builder::group);

        return Optional.of(builder.build());
    }

    private Optional<AccessLevel> extractAccessLevel(JsonNode node) {
        final JsonNode accessNode = node.get(NODE_ACCESS);
        if (accessNode != null && accessNode.isTextual()) {
            try {
                final AccessLevel access = AccessLevel.valueOf(accessNode.asText().toUpperCase());
                return Optional.of(access);
            } catch (IllegalArgumentException ex) {
                log.warn("Failed to parse organisation: Could not extract access level", ex);
            }
        } else {
            log.debug("Failed to extract access level from {}", node);
        }
        return Optional.empty();
    }

    private Optional<SecurityClassification> extractSecurityClassification(JsonNode node) {
        final JsonNode classificationNode = node.get(NODE_CLASSIFICATION);
        if (classificationNode != null && classificationNode.isTextual()) {
            try {
                final SecurityClassification classification = SecurityClassification.valueOf(
                        classificationNode.asText().toUpperCase());
                return Optional.of(classification);
            } catch (IllegalArgumentException ex) {
                log.warn("Failed to parse organisation: Could not extract security classification",
                         ex);
            }
        } else {
            log.debug("Failed to extract security classification from {}", node);
        }
        return Optional.empty();
    }

    private Optional<String> extractGroup(JsonNode node) {
        final JsonNode groupNode = node.get(NODE_GROUP);
        if (groupNode != null && groupNode.isTextual()) {
            return Optional.of(groupNode.asText().toLowerCase());
        } else {
            log.debug("Failed to extract group from {}", node);
        }
        return Optional.empty();
    }
}
