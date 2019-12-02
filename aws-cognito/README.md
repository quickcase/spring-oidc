# quickcase-spring-security / aws-cognito

Integration of Quickcase with AWS Cognito for Identity and Access Management.

## How to use

### On resource servers

#### 1. Import security configuration

Module `aws-cognito` for `quickcase-spring-security` provides a pre-configured `QuickcaseSecurityConfig` which can be
directly imported on the `ResourceServerConfiguration` as in example below:

```java
@Configuration
@EnableResourceServer
@Import(QuickcaseSecurityConfig.class)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .fullyAuthenticated();
    }

}
```

#### 2. Configure Spring Security

Must be configured:
* `security.oauth2.resource.user-info-uri`: Cognito's `/oauth2/userInfo` endpoint for the pool

Optionally required, in conjunction with `enable-machine` property:
* `security.oauth2.resource.jwk.key-set-uri`: Cognito's `/.well-known/jwks.json` endpoint for the pool

## Properties

### quickcase.security.aws-cognito.enable-machine

Whether to enable support for machine authentication (client credentials grant). `true` to enable, disabled by default.
When machine authentication is enabled, access token are always validated locally as a first step, and as optional second step the user claims are retrieved provided that the access token had the scope `profile`.

**When enabled**, both following Spring Security properties must be configured:
1. `security.oauth2.resource.user-info-uri`
2. `security.oauth2.resource.jwk.key-set-uri`

**When disabled**, only the user info endpoint (1) is required.

