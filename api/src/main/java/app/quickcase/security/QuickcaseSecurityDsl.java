package app.quickcase.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface QuickcaseSecurityDsl {
    HttpSecurity withQuickcaseSecurity(HttpSecurity http) throws Exception;
}
