package dev.gad.security.jwt;

public record JwtTokenPair(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInSeconds,
        long refreshTokenExpiresInSeconds) {
}
