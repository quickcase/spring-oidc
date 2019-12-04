# quickcase-spring-security / aws-cognito

Integration of Quickcase with AWS Cognito for Identity and Access Management.

## How to use

### On resource servers

#### 1. Add dependency

```groovy
implementation 'app.quickcase.security:aws-cognito:<version>'
```

#### 2. Import security configuration

The module `aws-cognito` for `quickcase-spring-security` provides a pre-configured `QuickcaseSecurityConfig` which can be
directly imported on the `WebSecurityConfigurerAdapter` as in example below:

```java
@Configuration
@EnableWebSecurity
@Import(QuickcaseSecurityConfig.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // ...
}
```

The `QuickcaseSecurityConfig` creates all the Spring beans needed to support use of Quickcase Security.

#### 3. Wire Quickcase DSL

In the `WebSecurityConfigurerAdapter`, wire an instance of `QuickcaseSecurityDsl`:

```java
@Autowired
private QuickcaseSecurityDsl quickcaseDsl;
```

#### 4. Configure

In the `WebSecurityConfigurerAdapter`, override the `configure(HttpSecurity http)` method and start the configuration with `quickcaseDsl.withQuickcaseSecurity(http)`:

```java
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        quickcaseDsl
            .withQuickcaseSecurity(http)
            // ...
            .authorizeRequests()
            .anyRequest()
            .authenticated();
    }
```

#### 5. Add required application properties

Must be configured:
* `quickcase.security.oidc.user-info-uri`: Cognito's `/oauth2/userInfo` endpoint for the pool
* `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`: Cognito's `/.well-known/jwks.json` endpoint for the pool

## Properties

### quickcase.security.aws-cognito.enable-machine

Whether to enable support for machine authentication (client credentials grant). `true` to enable, disabled by default.
When machine authentication is enabled, access token are always validated locally as a first step, and as optional second step the user claims are retrieved provided that the access token had the scope `profile`.

**When enabled**, both following Spring Security properties must be configured:
1. `security.oauth2.resource.user-info-uri`
2. `security.oauth2.resource.jwk.key-set-uri`

**When disabled**, only the user info endpoint (1) is required.

