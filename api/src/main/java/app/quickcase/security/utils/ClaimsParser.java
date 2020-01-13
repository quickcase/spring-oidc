package app.quickcase.security.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Optional;

public class ClaimsParser {
    private final Map<String, JsonNode> claims;

    public ClaimsParser(Map<String, JsonNode> claims) {
        this.claims = claims;
    }

    public Optional<String> getString(String claim) {
        return Optional.ofNullable(claims.get(claim))
                       .map(JsonNode::textValue);
    }
}
