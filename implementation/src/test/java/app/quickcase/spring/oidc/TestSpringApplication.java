package app.quickcase.spring.oidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring application posing as test double for consumer applications.
 */
@SpringBootApplication
public class TestSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestSpringApplication.class, args);
    }
}
