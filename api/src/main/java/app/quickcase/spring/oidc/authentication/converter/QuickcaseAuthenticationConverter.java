package app.quickcase.spring.oidc.authentication.converter;

import app.quickcase.spring.oidc.authentication.QuickcaseAuthentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;

public interface QuickcaseAuthenticationConverter extends Converter<Jwt, QuickcaseAuthentication> {
}
