package com.attachment_service.attachment_service.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // public if you want
                        .requestMatchers("/actuator/**").permitAll()

                        // example: projects - GET public, POST admin only (with @PreAuthorize)
                        .requestMatchers("/attachments/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, exception) -> {
                            log.error("❌ ACCESS DENIED - Path: {}, Method: {}, User: {}",
                                    request.getRequestURI(),
                                    request.getMethod(),
                                    request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "ANONYMOUS");
                            log.error("Exception: {}", exception.getMessage());
                            response.sendError(403, "Access Denied: " + exception.getMessage());
                        })
                        .authenticationEntryPoint((request, response, exception) -> {
                            log.error("❌ AUTHENTICATION FAILED - Path: {}, Method: {}",
                                    request.getRequestURI(),
                                    request.getMethod());
                            log.error("Exception: {}", exception.getMessage());
                            response.sendError(401, "Unauthorized: " + exception.getMessage());
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
