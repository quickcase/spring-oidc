package app.quickcase.spring.oidc.userinfo;

import java.util.Optional;

import app.quickcase.spring.oidc.OidcException;
import app.quickcase.spring.oidc.claims.ClaimsParser;
import app.quickcase.spring.oidc.claims.JsonClaimsParser;

import org.springframework.security.core.AuthenticationException;

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
        final ClaimsParser claims = new JsonClaimsParser(gateway.getClaims(accessToken));

        validateSubject(expectedSubject, claims);

        return extractor.extract(claims);
    }

    /**
     * Prevent token substitution attacks by validating sub claim.
     *
     * @param expectedSubject Subject expected by caller
     * @param claims Claims received from userInfo endpoint
     * @throws AuthenticationException When subjects cannot be compared or do not match.
     */
    private void validateSubject(String expectedSubject, ClaimsParser claims) {
        final Optional<String> actualSubject = claims.getString(CLAIM_SUB);

        if(actualSubject.isEmpty() || !expectedSubject.equals(actualSubject.get())) {
            throw new OidcException("User info subject does match expected subject");
        }
    }
}
