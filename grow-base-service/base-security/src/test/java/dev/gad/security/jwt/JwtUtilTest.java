package dev.gad.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.io.Encoders;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private static final Instant NOW = Instant.parse("2026-09-05T06:30:45Z");

    private JwtProperties properties;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        properties = properties();
        jwtUtil = new JwtUtil(properties, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void createsAndParsesAccessAndRefreshTokens() {
        JwtTokenPair pair = jwtUtil.createTokenPair("1001", Map.of("username", "admin"));

        Map<String, Object> accessClaims = jwtUtil.parseAccessToken(pair.accessToken());
        Map<String, Object> refreshClaims = jwtUtil.parseRefreshToken(pair.refreshToken());
        assertEquals("1001", accessClaims.get("sub"));
        assertEquals("admin", accessClaims.get("username"));
        assertEquals("access", accessClaims.get(JwtUtil.TOKEN_TYPE_CLAIM));
        assertEquals("refresh", refreshClaims.get(JwtUtil.TOKEN_TYPE_CLAIM));
        assertEquals(Duration.ofHours(2).toSeconds(), pair.accessTokenExpiresInSeconds());
        assertEquals(Duration.ofDays(7).toSeconds(), pair.refreshTokenExpiresInSeconds());
    }

    @Test
    void refreshesAccessTokenOnlyWithRefreshToken() {
        JwtTokenPair pair = jwtUtil.createTokenPair("1001");

        String refreshedAccessToken = jwtUtil.refreshAccessToken(
                pair.refreshToken(), Map.of("username", "updated-admin"));

        assertNotEquals(pair.accessToken(), refreshedAccessToken);
        assertEquals("1001", jwtUtil.getUserIdFromAccessToken(refreshedAccessToken));
        assertEquals("updated-admin",
                jwtUtil.parseAccessToken(refreshedAccessToken).get("username"));
        assertThrows(JwtAuthenticationException.class,
                () -> jwtUtil.refreshAccessToken(pair.accessToken()));
        assertThrows(JwtAuthenticationException.class,
                () -> jwtUtil.parseAccessToken(pair.refreshToken()));
    }

    @Test
    void rejectsExpiredToken() {
        String accessToken = jwtUtil.createAccessToken("1001");
        JwtUtil expiredJwtUtil = new JwtUtil(properties,
                Clock.fixed(NOW.plus(Duration.ofHours(3)), ZoneOffset.UTC));

        JwtAuthenticationException exception = assertThrows(
                JwtAuthenticationException.class,
                () -> expiredJwtUtil.parseAccessToken(accessToken));

        assertEquals("Token 已过期", exception.getMessage());
        assertEquals(401, exception.getCode());
    }

    @Test
    void rejectsReservedClaimsAndShortSecret() {
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.createAccessToken("1001", Map.of("sub", "other")));

        JwtProperties invalidProperties = properties();
        invalidProperties.setBase64Secret(Encoders.BASE64.encode(
                "short-secret".getBytes(StandardCharsets.UTF_8)));
        assertThrows(IllegalStateException.class, () -> new JwtUtil(invalidProperties));
    }

    private JwtProperties properties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("grow-admin-server-test");
        jwtProperties.setBase64Secret(Encoders.BASE64.encode(
                "grow-admin-test-jwt-secret-key-2026".getBytes(StandardCharsets.UTF_8)));
        jwtProperties.setAccessTokenTtl(Duration.ofHours(2));
        jwtProperties.setRefreshTokenTtl(Duration.ofDays(7));
        return jwtProperties;
    }
}
