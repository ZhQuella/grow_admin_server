package dev.gad.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.gad.security.jwt.JwtUtil;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class JwtReactiveAuthenticationManagerTest {

    @Test
    void createsAuthenticationWithPermissions() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.parseAccessToken("access-token")).thenReturn(Map.of(
                "sub", "1001",
                JwtUtil.PERMISSIONS_CLAIM, List.of("user:list", "user:create")));
        JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(jwtUtil);

        Authentication authentication = manager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        "access-token", "access-token"))
                .block();

        assertTrue(authentication.isAuthenticated());
        assertEquals("1001", authentication.getName());
        assertEquals(List.of("user:list", "user:create"), authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList());
    }

    @Test
    void rejectsMalformedPermissionsClaim() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.parseAccessToken("access-token")).thenReturn(Map.of(
                "sub", "1001",
                JwtUtil.PERMISSIONS_CLAIM, "user:list"));
        JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(jwtUtil);

        assertThrows(BadCredentialsException.class, () -> manager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        "access-token", "access-token"))
                .block());
    }
}
