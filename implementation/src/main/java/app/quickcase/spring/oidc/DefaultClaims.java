package app.quickcase.spring.oidc;

public interface DefaultClaims {
    String SUB = "sub";
    String NAME = "name";
    String EMAIL = "email";
    String APP_ROLES = "app_roles";
    String APP_ORGANISATIONS = "app_organisations";
    String USER_DEFAULT_JURISDICTION = "default_jurisdiction";
    String USER_DEFAULT_CASE_TYPE = "default_case_type";
    String USER_DEFAULT_STATE = "default_state";
}
