package com.tickets.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtEntryPoint jwtEntryPoint;
    private final JwtProvider jwtProvider;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtEntryPoint))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Públicos
                        .requestMatchers(
                                "/api/auth/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Comunicación interna entre microservicios
                        .requestMatchers(
                                HttpMethod.GET,
                                "/internal/usuarios/**"
                        ).authenticated()

                        // Perfil propio:
                        // cualquier usuario autenticado puede consultar
                        // y actualizar únicamente su propio perfil
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios/perfil"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/usuarios/perfil"
                        ).authenticated()

                        // Administración de usuarios y roles
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios/**",
                                "/api/roles/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios/**",
                                "/api/auth/registro"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/usuarios/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/usuarios/**"
                        ).hasRole("ADMIN")

                        // Todo lo que no esté declarado arriba se bloquea
                        .anyRequest().denyAll()
                );

        http.addFilterBefore(
                jwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}