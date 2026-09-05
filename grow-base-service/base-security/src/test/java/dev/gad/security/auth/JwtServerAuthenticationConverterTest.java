package dev.gad.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

class JwtServerAuthenticationConverterTest {

    private final JwtServerAuthenticationConverter converter =
            new JwtServerAuthenticationConverter();

    @Test
    void extractsBearerToken() {
        MockServerWebExchange exchange = exchangeWithAuthorization("Bearer access-token");

        Authentication authentication = converter.convert(exchange).block();

        assertEquals("access-token", authentication.getCredentials());
    }

    @Test
    void ignoresRequestWithoutBearerToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/protected"));

        assertNull(converter.convert(exchange).block());
    }

    @Test
    void rejectsEmptyBearerToken() {
        MockServerWebExchange exchange = exchangeWithAuthorization("Bearer ");

        assertThrows(BadCredentialsException.class, () -> converter.convert(exchange).block());
    }

    private MockServerWebExchange exchangeWithAuthorization(String authorization) {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/protected")
                .header(HttpHeaders.AUTHORIZATION, authorization));
    }
}
