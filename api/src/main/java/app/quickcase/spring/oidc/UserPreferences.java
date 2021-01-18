package app.quickcase.spring.oidc;

import lombok.Builder;
import lombok.Value;

/**
 * Provides QuickCase user's preferences.
 *
 * <p>
 * Hold user customisable settings, like their case list defaults.
 *
 * @author Valentin Laurin
 * @since 0.1
 */
@Value
@Builder
public class UserPreferences {
    private String defaultJurisdiction;
    private String defaultCaseType;
    private String defaultState;
}
