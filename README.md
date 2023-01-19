# spring-oidc
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build status](https://github.com/quickcase/spring-oidc/workflows/CI/badge.svg)](https://github.com/quickcase/spring-oidc/actions)

Provider-agnostic [OIDC](https://openid.net/connect/) integration using [Spring Security](https://spring.io/projects/spring-security) for QuickCase APIs.

## Supported OIDC providers

To achieve compatibility with the broadest range of OIDC providers, this library follows 2 guidelines:
- **Convention**: Follows the [OpenID Connect specs](https://openid.net/developers/specs/) as best as possible
- **Configuration**: Allow defaults from the OpenID Connect specs to be overridden through configuration (See [Configuration](#Configuration)).

In theory, this allows compatibility of QuickCase with any OIDC provider. In practice, the following integrations have been proven:
- [AWS Cognito](https://aws.amazon.com/cognito/)
- [Keycloak](https://www.keycloak.org/)

### Limitations

#### JWT access token

The current implementation mandates for the access token to be a JWT token with a `scope` claim which is used to drive support for [Client credentials grant](#client-credentials-grant).

This limitation will be lifted as part of [#59](https://github.com/quickcase/spring-oidc/issues/59).

## How to use

### Mode

The mode used controls where required claims are read from.
The following modes are currently supported:
- `user-info` (default): Extracts QuickCase claims from the OpenID UserInfo endpoint
- `jwt-access-token`: Extracts claims from the JWT Access token

The choice of the mode should be made depending on the OpenID Connect provider.
Some providers, such as Azure AD, do not support custom claims in UserInfo endpoint and thus can only be used with the mode `jwt-access-token`.
Other providers, such as AWS Cognito, do not support custom claims in access tokens and thus can only be used with the mode `user-info`.

The mode `jwt-access-token` offers better performances as it doesn't require an API call to be made to the UserInfo endpoint on the OpenID Connect provider.

### Private claims

QuickCase relies on each users having private claims exposed through OpenID Connect by the identity provider.

#### app.quickcase.claims/roles

Required, String. Comma-separated list of user roles used by QuickCase to enforce role-based ACLs.

#### app.quickcase.claims/organisations

Required, JSON String. JSON object containing the one or many organisations a user is granted access to. For each organisation are defined:

- `access`: one of `individual` (default), `group` or `organisation`
- `classification`: one of `public` (default), `private` or `restricted`
- `group`: string, group identifier, only required if access is `group`

Example:
```json
{
  "org-1": {"access": "organisation", "classification": "private"},
  "org-2": {"access": "group", "classification": "public", "group": "group-1"}
}
```

#### app.quickcase.claims/default_jurisdiction

Optional, String. Identifier of an organisation to load by default when the user first open QuickCase.

#### app.quickcase.claims/default_case_type

Optional, String. Identifier of a case type to load by default when the user first open QuickCase. This must be a valid case type of the default organisation.

#### app.quickcase.claims/default_state

Optional, String. Identifier of a state to load by default when the user first open QuickCase. This must be a valid state of the default case type.

### Client credentials grant

QuickCase supports OAuth2's [Client Credentials grant](https://tools.ietf.org/html/rfc6749#section-4.4) to allow for service-to-service interactions outside of the context of a specific user.

When using Client Credentials, user claims are **not** retrieved from the `/userinfo` endpoint. Instead, the `scope` claim of the access token is used as the list of roles used to challenge QuickCase's role-based ACLs.

By default, the decision to fetch `/userinfo` is controlled by the presence of the `openid` scope in the `scope` claim of the access token.

### Configuration

|Config|Default|Description|
|---|---|---|
|quickcase.oidc.mode <br> _QUICKCASE_OIDC_MODE_|`user-info`|Optional. Mode to use for integration with OIDC provider|
|quickcase.oidc.jwk-set-uri <br> _QUICKCASE_OIDC_JWKSETURI_||Required. URL of the OIDC provider's JWK set endpoint|
|quickcase.oidc.user-info-uri <br> _QUICKCASE_OIDC_USERINFOURI_||Required. URL of the OIDC provider's user info endpoint|
|quickcase.oidc.openid-scope <br> _QUICKCASE_OIDC_OPENIDSCOPE_|`openid`|Optional. Scope controlling whether `/userinfo` is queried to extract ID claims|
|quickcase.oidc.claims.prefix <br> _QUICKCASE_OIDC_CLAIMS_PREFIX_|empty string|Optional. Prefix to apply to all private claims|
|quickcase.oidc.claims.names.sub <br> _QUICKCASE_OIDC_CLAIMS_NAMES_SUB_|`sub`|Optional. Override name of OpenID `sub` claim from which subject is extracted|
|quickcase.oidc.claims.names.name <br> _QUICKCASE_OIDC_CLAIMS_NAMES_NAME_|`name`|Optional. Override name of OpenID `name` claim from which subject name is extracted|
|quickcase.oidc.claims.names.email <br> _QUICKCASE_OIDC_CLAIMS_NAMES_EMAIL_|`email`|Optional. Override name of OpenID `email` claim from which subject email is extracted|
|quickcase.oidc.claims.names.roles <br> _QUICKCASE_OIDC_CLAIMS_NAMES_ROLES_|`app.quickcase.claims/roles`|Optional. Override name of private roles claim from which subject's QuickCase roles are extracted|
|quickcase.oidc.claims.names.organisations <br> _QUICKCASE_OIDC_CLAIMS_NAMES_ORGANISATIONS_|`app.quickcase.claims/organisations`|Optional. Override name of private organisations claim from which subject's QuickCase organisation profiles are extracted|
|quickcase.oidc.claims.names.default-jurisdiction <br> _QUICKCASE_OIDC_CLAIMS_NAMES_DEFAULTJURISDICTION_|`app.quickcase.claims/default_jurisdiction` |Optional. Override name of private default jurisdiction claim from which subject's QuickCase UI preference is extracted|
|quickcase.oidc.claims.names.default-case-type <br> _QUICKCASE_OIDC_CLAIMS_NAMES_DEFAULTCASETYPE_|`app.quickcase.claims/default_case_type` |Optional. Override name of private default case type claim from which subject's QuickCase UI preference is extracted|
|quickcase.oidc.claims.names.default-state <br> _QUICKCASE_OIDC_CLAIMS_NAMES_DEFAULTSTATE_|`app.quickcase.claims/default_state` |Optional. Override name of private default state claim from which subject's QuickCase UI preference is extracted|
