package dev.gad.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gad.security.auth.JwtReactiveAuthenticationManager;
import dev.gad.security.auth.JwtServerAuthenticationConverter;
import dev.gad.security.handler.JsonSecurityErrorHandler;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

class SecurityConfigTest {

    @Test
    void skipsJwtAuthenticationForWhitelistedPaths() {
        JwtReactiveAuthenticationManager authenticationManager =
                mock(JwtReactiveAuthenticationManager.class);
        SecurityConfig config = new SecurityConfig();
        AuthenticationWebFilter filter = config.jwtAuthenticationWebFilter(
                authenticationManager,
                new JwtServerAuthenticationConverter(),
                new JsonSecurityErrorHandler(new ObjectMapper()));
        AtomicInteger chainInvocations = new AtomicInteger();

        List<String> whitelistedPaths = List.of(
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs",
                "/v3/api-docs/swagger-config",
                "/test/business-exception",
                "/account/captcha");
        for (String path : whitelistedPaths) {
            MockServerWebExchange exchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get(path)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"));

            filter.filter(exchange, currentExchange -> {
                chainInvocations.incrementAndGet();
                return currentExchange.getResponse().setComplete();
            }).block();
        }

        assertEquals(whitelistedPaths.size(), chainInvocations.get());
        verifyNoInteractions(authenticationManager);
    }
}
