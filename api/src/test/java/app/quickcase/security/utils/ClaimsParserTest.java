package app.quickcase.security.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("ClaimsParser")
class ClaimsParserTest {

    @Test
    @DisplayName("should return empty optional when claim missing")
    void claimMissing() {
        final ClaimsParser parser = new ClaimsParser(new HashMap<>());

        final Optional<String> missingClaim = parser.getString("any");

        assertThat(missingClaim.isEmpty(), is(true));
    }

    @Test
    @DisplayName("should return claim as String when present and textual")
    void claimPresentAsText() {
        final ClaimsParser parser = new ClaimsParser(mapWith("claim1", new TextNode("value1")));

        final Optional<String> claim1 = parser.getString("claim1");

        assertThat(claim1.orElseThrow(), equalTo("value1"));
    }

    @Test
    @DisplayName("should return empty optional when present but not textual")
    void claimPresentAsObject() {
        final ClaimsParser parser = new ClaimsParser(mapWith("claim1", new ObjectNode(new JsonNodeFactory(true))));

        final Optional<String> claim1 = parser.getString("claim1");

        assertThat(claim1.isEmpty(), is(true));
    }

    private Map<String, JsonNode> mapWith(String key, JsonNode value) {
        final HashMap<String, JsonNode> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}