package app.quickcase.security.cognito;

public interface CognitoClaims {
    String SUB = "sub";
    String NAME = "name";
    String EMAIL = "email";
    String APP_ROLES = "custom:app_roles";
}
