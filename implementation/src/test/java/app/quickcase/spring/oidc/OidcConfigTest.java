package app.quickcase.spring.oidc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@EnableConfigurationProperties(OidcConfig.class)
class OidcConfigTest {

    @Autowired
    private OidcConfig oidcConfig;

    @Test
    @DisplayName("should provide user-info-uri")
    void shouldProvideRootConfiguration() {
        assertThat(oidcConfig.getUserInfoUri(), equalTo("https://oidc.provider/userinfo"));
    }

}