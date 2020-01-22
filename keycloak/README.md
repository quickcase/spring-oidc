# quickcase-spring-security / keycloak

Integration of Quickcase with KeyCloak for Identity and Access Management.

## How to use

### On resource servers

#### 1. Add dependency

```groovy
implementation 'app.quickcase.security:keycloak:<version>'
```

#### 2. Import security configuration

The module `keycloak` for `quickcase-spring-security` provides a pre-configured `QuickcaseSecurityConfig` which can be
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
* `quickcase.security.oidc.user-info-uri`: KeyCloak's `/auth/realms/<REALM_ID>/protocol/openid-connect/userinfo` endpoint for the pool
* `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`: KeyCloak's `/auth/realms/<REALM_ID>/protocol/openid-connect/certs` endpoint for the pool


