package dev.gad.security.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

class JsonSecurityErrorHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSecurityErrorHandler handler =
            new JsonSecurityErrorHandler(objectMapper);

    @Test
    void returnsUnauthorizedJson() throws Exception {
        MockServerWebExchange exchange = exchange();

        handler.commence(exchange, new BadCredentialsException("invalid token")).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertJsonBody(exchange, 401, "请先登录");
    }

    @Test
    void returnsForbiddenJson() throws Exception {
        MockServerWebExchange exchange = exchange();

        handler.handle(exchange, new AccessDeniedException("denied")).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        assertJsonBody(exchange, 403, "没有权限");
    }

    private void assertJsonBody(
            MockServerWebExchange exchange, int code, String message) throws Exception {
        JsonNode body = objectMapper.readTree(exchange.getResponse().getBodyAsString().block());
        assertEquals(code, body.get("code").asInt());
        assertEquals(message, body.get("message").asText());
        assertTrue(body.get("data").isNull());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/protected"));
    }
}
