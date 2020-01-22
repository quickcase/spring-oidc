package app.quickcase.security.organisation;

import java.util.Map;

public interface OrganisationProfilesParser<T> {
    Map<String, OrganisationProfile> parse(T source);
}
