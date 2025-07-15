package com.plazoleta.trazabilidad.infrastructure.security;

import com.plazoleta.trazabilidad.domain.exceptions.UnauthorizedException;
import com.plazoleta.trazabilidad.domain.util.contants.DomainConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Long getRestaurantIdFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        if (auth.getPrincipal() instanceof Claims claims) {
            Number rest = claims.get("restaurantId", Number.class);
            if (rest == null) {
                throw new UnauthorizedException("Este usuario no tiene asociado ningún restaurante");
            }
            return rest.longValue();
        }
        throw new UnauthorizedException("Principal inválido para extraer restaurante");
    }

    public Long getOwnerIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Claims)) {
            return null;
        }

        Claims claims = (Claims) authentication.getPrincipal();
        return claims.get(DomainConstants.OWNER_ID, Long.class);
    }

    public Long getClientIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Claims)) {
            return null;
        }

        Claims claims = (Claims) authentication.getPrincipal();
        return claims.get(DomainConstants.CLIENT_ID, Long.class);
    }
}