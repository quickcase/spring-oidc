# spring-oidc standard implementation

Configuration-driven, generic implementation of OIDC for QuickCase, compatible with most OIDC providers.

This documentation is meant for developers of QuickCase resource servers.

## How to: Use in a resource server

### 1. Add dependency

```groovy
implementation 'app.quickcase.spring:oidc:<version>'
```

### 2. Import security configuration

This module provides a pre-configured `QuickcaseSecurityConfig` which can be
directly imported on the `WebSecurityConfigurerAdapter` as in example below:

```java
@Configuration
@EnableWebSecurity
@Import(QuickcaseSecurityConfig.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // ...
}
```

The `QuickcaseSecurityConfig` creates all the Spring beans needed to support the use of OIDC for QuickCase with Spring Security.

### 3. Wire Quickcase DSL

In the `WebSecurityConfigurerAdapter`, wire an instance of `QuickcaseSecurityDsl`:

```java
@Autowired
private QuickcaseSecurityDsl quickcaseDsl;
```

### 4. Configure

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
