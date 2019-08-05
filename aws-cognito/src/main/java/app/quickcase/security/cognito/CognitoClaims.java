package app.quickcase.security.cognito;

public interface CognitoClaims {
    String SUB = "sub";
    String NAME = "name";
    String EMAIL = "email";
    String APP_ROLES = "custom:app_roles";
    String APP_JURISDICTIONS = "custom:jurisdictions";
    String USER_DEFAULT_JURISDICTION = "custom:default_jurisdiction";
    String USER_DEFAULT_CASE_TYPE = "custom:default_case_type";
    String USER_DEFAULT_STATE = "custom:default_state";
}
