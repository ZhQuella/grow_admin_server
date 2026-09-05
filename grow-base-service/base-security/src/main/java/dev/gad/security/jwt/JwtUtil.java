package dev.gad.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String PERMISSIONS_CLAIM = "permissions";

    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final int MINIMUM_SECRET_BYTES = 32;
    private static final Set<String> RESERVED_CLAIMS = Set.of(
            Claims.ISSUER, Claims.SUBJECT, Claims.AUDIENCE, Claims.EXPIRATION,
            Claims.NOT_BEFORE, Claims.ISSUED_AT, Claims.ID, TOKEN_TYPE_CLAIM);

    private final JwtProperties properties;
    private final SecretKey signingKey;
    private final Clock clock;

    @Autowired
    public JwtUtil(JwtProperties properties) {
        this(properties, Clock.systemUTC());
    }

    JwtUtil(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        this.signingKey = createSigningKey(properties.getBase64Secret());
        validateTtl(properties.getAccessTokenTtl(), "Access Token");
        validateTtl(properties.getRefreshTokenTtl(), "Refresh Token");
    }

    public JwtTokenPair createTokenPair(String userId) {
        return createTokenPair(userId, Map.of());
    }

    public JwtTokenPair createTokenPair(String userId, Map<String, ?> accessClaims) {
        return new JwtTokenPair(
                createAccessToken(userId, accessClaims),
                createRefreshToken(userId),
                properties.getAccessTokenTtl().toSeconds(),
                properties.getRefreshTokenTtl().toSeconds());
    }

    public String createAccessToken(String userId) {
        return createAccessToken(userId, Map.of());
    }

    public String createAccessToken(String userId, Map<String, ?> claims) {
        return createToken(userId, ACCESS_TOKEN_TYPE, properties.getAccessTokenTtl(), claims);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, REFRESH_TOKEN_TYPE, properties.getRefreshTokenTtl(), Map.of());
    }

    public String refreshAccessToken(String refreshToken) {
        return refreshAccessToken(refreshToken, Map.of());
    }

    public String refreshAccessToken(String refreshToken, Map<String, ?> accessClaims) {
        Claims claims = parseToken(refreshToken, REFRESH_TOKEN_TYPE);
        return createAccessToken(claims.getSubject(), accessClaims);
    }

    public Map<String, Object> parseAccessToken(String token) {
        return copyClaims(parseToken(token, ACCESS_TOKEN_TYPE));
    }

    public Map<String, Object> parseRefreshToken(String token) {
        return copyClaims(parseToken(token, REFRESH_TOKEN_TYPE));
    }

    public String getUserIdFromAccessToken(String token) {
        return parseToken(token, ACCESS_TOKEN_TYPE).getSubject();
    }

    public String getUserIdFromRefreshToken(String token) {
        return parseToken(token, REFRESH_TOKEN_TYPE).getSubject();
    }

    private String createToken(
            String userId, String tokenType, Duration ttl, Map<String, ?> additionalClaims) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        validateAdditionalClaims(additionalClaims);

        Instant issuedAt = clock.instant();
        JwtBuilder builder = Jwts.builder();
        additionalClaims.forEach(builder::claim);
        return builder
                .issuer(properties.getIssuer())
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(ttl)))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .signWith(signingKey)
                .compact();
    }

    private Claims parseToken(String token, String expectedTokenType) {
        if (token == null || token.isBlank()) {
            throw new JwtAuthenticationException("Token 不能为空");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.getIssuer())
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            if (!expectedTokenType.equals(tokenType)) {
                throw new JwtAuthenticationException("Token 类型错误");
            }
            return claims;
        } catch (ExpiredJwtException exception) {
            throw new JwtAuthenticationException("Token 已过期");
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException("Token 无效");
        }
    }

    private SecretKey createSigningKey(String base64Secret) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(base64Secret);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("JWT Base64 密钥格式错误", exception);
        }
        if (keyBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException("JWT Base64 密钥解码后不能少于32字节");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Map<String, Object> copyClaims(Claims claims) {
        return Collections.unmodifiableMap(new HashMap<>(claims));
    }

    private void validateAdditionalClaims(Map<String, ?> claims) {
        if (claims == null) {
            throw new IllegalArgumentException("JWT 扩展字段不能为空");
        }
        if (claims.keySet().stream().anyMatch(RESERVED_CLAIMS::contains)) {
            throw new IllegalArgumentException("JWT 扩展字段不能覆盖标准字段");
        }
    }

    private void validateTtl(Duration ttl, String tokenName) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalStateException(tokenName + "有效期必须大于0");
        }
    }
}
