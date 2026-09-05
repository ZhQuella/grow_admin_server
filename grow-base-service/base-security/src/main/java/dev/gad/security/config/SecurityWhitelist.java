package dev.gad.security.config;

public final class SecurityWhitelist {

    private static final String[] PATHS = {
        "/swagger-ui.html",
        "/webjars/swagger-ui/**",
        "/v3/api-docs/**",
        "/test/**",
        "/account/captcha",
    };

    private SecurityWhitelist() {
    }

    public static String[] paths() {
        return PATHS.clone();
    }
}
