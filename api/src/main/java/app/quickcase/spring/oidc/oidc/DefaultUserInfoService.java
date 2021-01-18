package app.quickcase.spring.oidc.oidc;

import app.quickcase.spring.oidc.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public class DefaultUserInfoService implements UserInfoService {
    private static final String CLAIM_SUB = "sub";

    private final UserInfoGateway gateway;
    private final UserInfoExtractor extractor;

    public DefaultUserInfoService(UserInfoGateway gateway, UserInfoExtractor extractor) {
        this.gateway = gateway;
        this.extractor = extractor;
    }

    @Override
    public UserInfo loadUserInfo(String expectedSubject, String accessToken) {
        final Map<String, JsonNode> claims = gateway.getClaims(accessToken);

        validateSubject(expectedSubject, claims.get(CLAIM_SUB).textValue());

        return extractor.extract(claims);
    }

    /**
     * Prevent token substitution attacks by validating sub claim.
     *
     * @param expectedSubject Subject expected by caller
     * @param actualSubject Subject received from userInfo endpoint
     * @throws AuthenticationException When subjects cannot be compared or do not match.
     */
    private void validateSubject(String expectedSubject, Object actualSubject) {
        if(!expectedSubject.equals(actualSubject)) {
            throw new OidcException("User info subject does match expected subject");
        }
    }
}
