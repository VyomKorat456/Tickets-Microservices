package com.ticket_service.ticket_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        log.info("=== JWT AUTHENTICATION FILTER ===");
        log.info("Request: {} {}", method, requestPath);

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ No valid Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.info("✓ Bearer token found, length: {}", token.length());

        if (!jwtUtil.isTokenValid(token)) {
            log.error("❌ JWT Token validation failed - signature mismatch or expired");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("✓ JWT Token is valid");

        String username = jwtUtil.extractUsername(token);
        List<String> roles = jwtUtil.extractRoles(token);

        log.info("✓ Extracted username: {}", username);
        log.info("✓ Extracted roles: {}", roles);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            log.info("✓ Created {} granted authorities: {}", authorities.size(), 
                    authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("✓ SecurityContext authentication set for user: {}", username);
        } else if (username == null) {
            log.warn("❌ Username is null");
        } else {
            log.warn("⚠ Authentication already exists in SecurityContext");
        }

        filterChain.doFilter(request, response);
    }
}

