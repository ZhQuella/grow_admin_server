package dev.gad.security.auth;

import dev.gad.security.jwt.JwtAuthenticationException;
import dev.gad.security.jwt.JwtUtil;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    public JwtReactiveAuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            Object credentials = authentication.getCredentials();
            if (!(credentials instanceof String token) || token.isBlank()) {
                throw new IllegalArgumentException("Token 不能为空");
            }
            Map<String, Object> claims = jwtUtil.parseAccessToken(token);
            String userId = getUserId(claims);
            List<GrantedAuthority> authorities = getAuthorities(claims);
            return Mono.just(UsernamePasswordAuthenticationToken.authenticated(
                    userId, null, authorities));
        } catch (JwtAuthenticationException | IllegalArgumentException exception) {
            return Mono.error(new BadCredentialsException("Token 认证失败", exception));
        }
    }

    private String getUserId(Map<String, Object> claims) {
        Object subject = claims.get("sub");
        if (!(subject instanceof String userId) || userId.isBlank()) {
            throw new IllegalArgumentException("Token 中缺少用户ID");
        }
        return userId;
    }

    private List<GrantedAuthority> getAuthorities(Map<String, Object> claims) {
        Object permissionsClaim = claims.get(JwtUtil.PERMISSIONS_CLAIM);
        if (permissionsClaim == null) {
            return List.of();
        }
        if (!(permissionsClaim instanceof Collection<?> permissions)) {
            throw new IllegalArgumentException("Token 权限字段格式错误");
        }

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (Object permission : permissions) {
            if (!(permission instanceof String value) || value.isBlank()) {
                throw new IllegalArgumentException("Token 权限字段格式错误");
            }
            authorities.add(new SimpleGrantedAuthority(value));
        }
        return List.copyOf(authorities);
    }
}
