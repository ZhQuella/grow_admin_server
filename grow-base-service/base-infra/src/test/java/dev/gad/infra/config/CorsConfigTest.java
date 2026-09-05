package dev.gad.infra.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.reactive.CorsWebFilter;

class CorsConfigTest {

    private CorsWebFilter corsWebFilter;

    @BeforeEach
    void setUp() {
        CorsConfig config = new CorsConfig();
        CorsProperties properties = new CorsProperties();
        properties.setAllowedOriginPatterns(List.of("https://frontend.example"));
        corsWebFilter = config.corsWebFilter(config.corsConfigurationSource(properties));
    }

    @Test
    void handlesPreflightRequest() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("https://api.example/test/business-exception")
                        .header(HttpHeaders.ORIGIN, "https://frontend.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.POST.name())
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        AtomicBoolean chainInvoked = new AtomicBoolean();

        corsWebFilter.filter(exchange, currentExchange -> {
            chainInvoked.set(true);
            return currentExchange.getResponse().setComplete();
        }).block();

        HttpHeaders headers = exchange.getResponse().getHeaders();
        assertEquals("https://frontend.example",
                headers.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS",
                headers.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS));
        assertEquals("Authorization, Content-Type",
                headers.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS));
        assertEquals("true", headers.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
        assertFalse(chainInvoked.get());
    }
}
