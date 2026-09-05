package dev.gad.security.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gad.common.result.Result;
import dev.gad.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JsonSecurityErrorHandler
        implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSecurityErrorHandler.class);

    private final ObjectMapper objectMapper;

    public JsonSecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(
            ServerWebExchange exchange, AuthenticationException exception) {
        LOGGER.warn("Authentication failed: {}", exception.getMessage());
        return writeResponse(exchange, HttpStatus.UNAUTHORIZED, ResultCode.UNAUTHORIZED);
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange, AccessDeniedException exception) {
        LOGGER.warn("Access denied: {}", exception.getMessage());
        return writeResponse(exchange, HttpStatus.FORBIDDEN, ResultCode.FORBIDDEN);
    }

    private Mono<Void> writeResponse(
            ServerWebExchange exchange, HttpStatus status, ResultCode resultCode) {
        try {
            byte[] body = objectMapper.writeValueAsBytes(Result.fail(resultCode));
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException exception) {
            return Mono.error(exception);
        }
    }
}
