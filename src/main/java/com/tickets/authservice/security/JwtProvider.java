package com.tickets.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration:3600000}")
    private int expiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
        List<String> authorities = usuarioPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .id(usuarioPrincipal.getIdUsuario().toString())
                .subject(usuarioPrincipal.getUsername())
                .claim("idUsuario", usuarioPrincipal.getIdUsuario())
                .claim("nombres", usuarioPrincipal.getNombres())
                .claim("authorities", authorities)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getCorreoFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token mal formado");
        } catch (UnsupportedJwtException e) {
            log.error("Token no soportado");
        } catch (ExpiredJwtException e) {
            log.error("Token expirado");
        } catch (IllegalArgumentException e) {
            log.error("Token vacío");
        } catch (SignatureException e) {
            log.error("Firma de token inválida");
        }
        return false;
    }
}