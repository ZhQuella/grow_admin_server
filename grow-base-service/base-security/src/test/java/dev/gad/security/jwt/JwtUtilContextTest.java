package dev.gad.security.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class JwtUtilContextTest {

    private static final String TEST_SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Test
    void registersJwtUtilBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(JwtProperties.class, JwtUtilContextTest::jwtProperties);
            context.register(JwtUtil.class);
            context.refresh();

            assertNotNull(context.getBean(JwtUtil.class));
        }
    }

    private static JwtProperties jwtProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("test-issuer");
        properties.setBase64Secret(TEST_SECRET);
        properties.setAccessTokenTtl(Duration.ofMinutes(30));
        properties.setRefreshTokenTtl(Duration.ofDays(7));
        return properties;
    }
}
