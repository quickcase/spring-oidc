package app.quickcase.security.cognito;

import app.quickcase.security.authentication.QuickcaseAuthentication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * AWS Cognito presents the following constraints:
 * <ul>
 *     <li>The access token cannot be customised to include custom attributes</li>
 *     <li>The access token does not contain user claims</li>
 *     <li>The access token does not have authorities</li>
 * </ul>
 *
 * This is a problem for QuickCase which heavily relies on user metadata like name, email and roles
 * for fine-grained access control and audit.
 * <br/><br/>
 * There are 2 ways to address this issue: either send the ID token along with the access token, or
 * call the /userInfo endpoint with the access token to retrieve the user's claims. The second
 * approach was chosen as it aligns with the native Spring Security implementation through the use
 * of {@link UserInfoTokenServices}.
 * <br/><br/>
 * However, when it comes to machine-to-machine calls authentication is still required by QuickCase
 * and takes the form of an OAuth2 client credential grant. In that situation, additional complexity
 * arises from the absence of user profile (client credential grant is not part of OIDC): the
 * /userInfo cannot be called and no user claims can be retrieved.
 * <br/><br/>
 * To allow both user-to-machine and machine-to-machine requests to be handled, this implementation
 * favoured verifying the access token as a mandatory first step, and then query the /userInfo
 * endpoint as an optional second step conditioned by the presence of Cognito's `profile` scope
 * on the access token.
 * An abstraction is added by {@link QuickcaseAuthentication} to encourage consistent behaviour for
 * both users and clients by, for example, populating roles from claims for user and from scopes for
 * clients.
 *
 */
public class CognitoTokenServices implements ResourceServerTokenServices {

    private final DefaultTokenServices defaultServices;
    private final UserInfoTokenServices userInfoServices;

    public CognitoTokenServices(DefaultTokenServices defaultServices,
                                UserInfoTokenServices userInfoServices) {
        this.defaultServices = defaultServices;
        this.userInfoServices = userInfoServices;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        final OAuth2Authentication clientAuthentication = defaultServices.loadAuthentication(accessToken);

        OAuth2Authentication userAuthentication = null;
        if (clientAuthentication.getOAuth2Request().getScope().contains("profile")) {
            userAuthentication = userInfoServices.loadAuthentication(accessToken);
        }

        return new QuickcaseAuthentication(clientAuthentication.getOAuth2Request(), userAuthentication != null ? userAuthentication.getUserAuthentication() : null);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return defaultServices.readAccessToken(accessToken);
    }
}
