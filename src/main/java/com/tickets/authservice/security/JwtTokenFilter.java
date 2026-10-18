package com.tickets.authservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getToken(request);
            if (token != null && jwtProvider.validateToken(token)) {
                Claims claims = jwtProvider.getClaims(token);
                String correo = claims.getSubject();
                Integer idUsuario = claims.get("idUsuario", Integer.class);

                List<String> authorities = claims.get("authorities", List.class);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        correo,
                        null,
                        authorities.stream().map(SimpleGrantedAuthority::new).toList()
                );

                auth.setDetails(idUsuario);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            log.error("Fallo en el método doFilterInternal: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}